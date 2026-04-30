package com.ecommerce.security_lib.filter;

import com.ecommerce.security_lib.model.AuthenticatedUser;
import com.ecommerce.security_lib.service.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                String email  = jwtService.extractEmail(token);
                String role   = jwtService.extractRole(token);
                Long   userId = jwtService.extractUserId(token);

                AuthenticatedUser authUser = AuthenticatedUser
                        .builder()
                        .userId(userId)
                        .email(email)
                        .role(role)
                        .build();

                var auth = new UsernamePasswordAuthenticationToken(
                        authUser,
                        null,
                        List.of(new SimpleGrantedAuthority(
                                "ROLE_" + role))
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(auth);

                log.debug("Authenticated: {} role: {}",
                        email, role);
            }
        } catch (Exception e) {
            log.error("JWT error: {}", e.getMessage());
        }

        chain.doFilter(request, response);
    }
}