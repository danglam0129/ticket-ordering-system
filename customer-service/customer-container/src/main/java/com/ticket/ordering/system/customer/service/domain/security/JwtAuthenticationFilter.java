package com.ticket.ordering.system.customer.service.domain.security;

import com.ticket.ordering.system.customer.service.domain.dto.token.JwtTokenClaims;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null &&
                authorizationHeader.startsWith(BEARER_PREFIX) &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            Optional<JwtTokenClaims> claims = jwtTokenService.parseAccessToken(token);
            claims.ifPresent(jwtTokenClaims -> {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        jwtTokenClaims.getUsername(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + jwtTokenClaims.getRole().name())));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }
        filterChain.doFilter(request, response);
    }
}
