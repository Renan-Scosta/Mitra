package com.mitra.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RateLimitFilterTest {

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() throws Exception {
        filter = new RateLimitFilter();

        // Inject @Value fields via reflection since we're not using Spring context
        var loginRequestsField = RateLimitFilter.class.getDeclaredField("loginRequests");
        loginRequestsField.setAccessible(true);
        loginRequestsField.setInt(filter, 3); // Low limit for testing

        var loginMinutesField = RateLimitFilter.class.getDeclaredField("loginMinutes");
        loginMinutesField.setAccessible(true);
        loginMinutesField.setInt(filter, 1);

        var registerRequestsField = RateLimitFilter.class.getDeclaredField("registerRequests");
        registerRequestsField.setAccessible(true);
        registerRequestsField.setInt(filter, 2); // Low limit for testing

        var registerMinutesField = RateLimitFilter.class.getDeclaredField("registerMinutes");
        registerMinutesField.setAccessible(true);
        registerMinutesField.setInt(filter, 1);
    }

    @Test
    @DisplayName("Should allow request when bucket has tokens")
    void shouldAllowRequestWhenBucketHasTokens() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilterInternal(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader("X-Rate-Limit-Remaining")).isNotNull();
        assertThat(Integer.parseInt(response.getHeader("X-Rate-Limit-Remaining")))
                .isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should block request when bucket is exhausted and return 429")
    void shouldBlockRequestWhenBucketExhausted() throws Exception {
        String clientIp = "10.0.0.1";

        // Exhaust the login bucket (limit=3)
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest request =
                    new MockHttpServletRequest("POST", "/api/v1/auth/login");
            request.setRemoteAddr(clientIp);
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, new MockFilterChain());
            assertThat(response.getStatus()).isEqualTo(200);
        }

        // 4th request should be blocked
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr(clientIp);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilterInternal(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader("Retry-After")).isNotNull();
        assertThat(response.getHeader("X-Rate-Limit-Remaining")).isEqualTo("0");
        assertThat(response.getContentType()).isEqualTo("application/json");

        String content = response.getContentAsString();
        assertThat(content).contains("Too Many Requests");
        assertThat(content).contains("Rate limit exceeded");
    }

    @Test
    @DisplayName("Should not rate-limit non-protected endpoints")
    void shouldNotRateLimitNonProtectedEndpoints() throws Exception {
        for (int i = 0; i < 50; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/routines");
            request.setRemoteAddr("10.0.0.2");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, new MockFilterChain());
            assertThat(response.getStatus()).isEqualTo(200);
        }
    }

    @Test
    @DisplayName("Should not rate-limit GET requests")
    void shouldNotRateLimitGetRequests() throws Exception {
        for (int i = 0; i < 50; i++) {
            MockHttpServletRequest request =
                    new MockHttpServletRequest("GET", "/api/v1/auth/login");
            request.setRemoteAddr("10.0.0.3");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilterInternal(request, response, new MockFilterChain());
            assertThat(response.getStatus()).isEqualTo(200);
        }
    }

    @Test
    @DisplayName("Should isolate buckets per IP address")
    void shouldIsolateBucketsPerIp() throws Exception {
        // Exhaust bucket for IP-A
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest request =
                    new MockHttpServletRequest("POST", "/api/v1/auth/login");
            request.setRemoteAddr("10.0.0.10");
            filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());
        }

        // IP-A should be blocked
        MockHttpServletRequest blockedRequest =
                new MockHttpServletRequest("POST", "/api/v1/auth/login");
        blockedRequest.setRemoteAddr("10.0.0.10");
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilterInternal(blockedRequest, blockedResponse, new MockFilterChain());
        assertThat(blockedResponse.getStatus()).isEqualTo(429);

        // IP-B should still be allowed
        MockHttpServletRequest allowedRequest =
                new MockHttpServletRequest("POST", "/api/v1/auth/login");
        allowedRequest.setRemoteAddr("10.0.0.20");
        MockHttpServletResponse allowedResponse = new MockHttpServletResponse();
        filter.doFilterInternal(allowedRequest, allowedResponse, new MockFilterChain());
        assertThat(allowedResponse.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should apply separate limits for register endpoint")
    void shouldApplySeparateLimitsForRegister() throws Exception {
        String clientIp = "10.0.0.30";

        // Exhaust the register bucket (limit=2)
        for (int i = 0; i < 2; i++) {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/users");
            request.setRemoteAddr(clientIp);
            filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());
        }

        // 3rd register request should be blocked
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/users");
        request.setRemoteAddr(clientIp);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilterInternal(request, response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(429);

        // But login for the same IP should still work (different bucket map)
        MockHttpServletRequest loginRequest =
                new MockHttpServletRequest("POST", "/api/v1/auth/login");
        loginRequest.setRemoteAddr(clientIp);
        MockHttpServletResponse loginResponse = new MockHttpServletResponse();
        filter.doFilterInternal(loginRequest, loginResponse, new MockFilterChain());
        assertThat(loginResponse.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Should resolve IP from X-Forwarded-For header")
    void shouldResolveIpFromXForwardedFor() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18");

        String resolvedIp = filter.resolveClientIp(request);
        assertThat(resolvedIp).isEqualTo("203.0.113.50");
    }

    @Test
    @DisplayName("Should fallback to RemoteAddr when X-Forwarded-For is absent")
    void shouldFallbackToRemoteAddr() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/login");
        request.setRemoteAddr("192.168.1.100");

        String resolvedIp = filter.resolveClientIp(request);
        assertThat(resolvedIp).isEqualTo("192.168.1.100");
    }

    @Test
    @DisplayName("Should rate-limit Google login endpoint")
    void shouldRateLimitGoogleLoginEndpoint() throws Exception {
        String clientIp = "10.0.0.40";

        // Exhaust the login bucket (limit=3, shared with /auth/login)
        for (int i = 0; i < 3; i++) {
            MockHttpServletRequest request =
                    new MockHttpServletRequest("POST", "/api/v1/auth/google");
            request.setRemoteAddr(clientIp);
            filter.doFilterInternal(request, new MockHttpServletResponse(), new MockFilterChain());
        }

        // 4th request should be blocked
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/google");
        request.setRemoteAddr(clientIp);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilterInternal(request, response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(429);
    }
}
