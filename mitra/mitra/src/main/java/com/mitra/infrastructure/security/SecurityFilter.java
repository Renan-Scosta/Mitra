package com.mitra.infrastructure.security;

import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepositoryPort userRepositoryPort;

    public SecurityFilter(TokenService tokenService, UserRepositoryPort userRepositoryPort) {
        this.tokenService = tokenService;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        var token = this.recoverToken(request);
        if (token != null) {
            log.debug("Processing authentication token for {} {}", request.getMethod(), request.getRequestURI());
            String email = tokenService.validateToken(token);
            
            if (email != null && !email.isEmpty()) {
                Optional<User> userOpt = userRepositoryPort.findByEmail(email);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, List.of(authority));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authenticated userId={} email={}", user.getId(), user.getEmail());
                } else {
                    log.warn("Token valid but user not found: email={}", email);
                }
            } else {
                log.warn("Invalid token received for {} {}", request.getMethod(), request.getRequestURI());
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
