package com.mitra.infrastructure.security;

import com.mitra.application.port.out.UserRepositoryPort;
import com.mitra.domain.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

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
            String email = tokenService.validateToken(token);
            
            if (email != null && !email.isEmpty()) {
                Optional<User> userOpt = userRepositoryPort.findByEmail(email);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    // We don't have roles implemented yet, passing an empty list
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
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
