package com.mitra.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting filter that protects public endpoints against brute-force attacks.
 *
 * <p>Uses the Token Bucket algorithm (via Bucket4j) with per-IP isolation.
 * When the limit is exceeded, the filter writes a 429 response directly
 * (filters run before the DispatcherServlet, so @ControllerAdvice cannot catch filter exceptions).
 */
@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentHashMap<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${rate-limit.login.requests:10}")
    private int loginRequests;

    @Value("${rate-limit.login.minutes:1}")
    private int loginMinutes;

    @Value("${rate-limit.register.requests:5}")
    private int registerRequests;

    @Value("${rate-limit.register.minutes:1}")
    private int registerMinutes;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Only rate-limit specific POST endpoints
        if (!"POST".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        ConcurrentHashMap<String, Bucket> bucketMap = resolveBucketMap(path);
        if (bucketMap == null) {
            // Not a rate-limited endpoint
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = resolveClientIp(request);
        Bucket bucket = bucketMap.computeIfAbsent(clientIp, key -> createBucket(path));

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long retryAfterSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000 + 1;
            log.warn("Rate limit exceeded for IP={} on path={} — retry after {}s", clientIp, path, retryAfterSeconds);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.addHeader("X-Rate-Limit-Remaining", "0");

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", LocalDateTime.now().toString());
            body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
            body.put("error", "Too Many Requests");
            body.put("message", "Rate limit exceeded. Try again in " + retryAfterSeconds + " seconds.");

            objectMapper.writeValue(response.getOutputStream(), body);
            // Do NOT call filterChain.doFilter — stop the request here
        }
    }

    /**
     * Resolves which bucket map to use based on the request path.
     * Returns null if the path is not rate-limited.
     */
    private ConcurrentHashMap<String, Bucket> resolveBucketMap(String path) {
        if (path.equals("/api/v1/auth/login") || path.equals("/api/v1/auth/google")) {
            return loginBuckets;
        }
        if (path.equals("/api/v1/users")) {
            return registerBuckets;
        }
        return null;
    }

    /**
     * Creates a new bucket with the appropriate configuration for the given path.
     */
    private Bucket createBucket(String path) {
        if (path.equals("/api/v1/users")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.simple(registerRequests, Duration.ofMinutes(registerMinutes)))
                    .build();
        }
        // Default: login/google endpoints
        return Bucket.builder()
                .addLimit(Bandwidth.simple(loginRequests, Duration.ofMinutes(loginMinutes)))
                .build();
    }

    /**
     * Resolves the client IP address, honoring the X-Forwarded-For header
     * (set by reverse proxies like Nginx) with fallback to RemoteAddr.
     */
    String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // X-Forwarded-For can contain multiple IPs; the first one is the client
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Exposes bucket maps for testing purposes.
     */
    ConcurrentHashMap<String, Bucket> getLoginBuckets() {
        return loginBuckets;
    }

    ConcurrentHashMap<String, Bucket> getRegisterBuckets() {
        return registerBuckets;
    }
}
