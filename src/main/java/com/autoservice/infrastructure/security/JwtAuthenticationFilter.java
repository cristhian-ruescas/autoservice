package com.autoservice.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/")
                || path.startsWith("/actuator/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.isClientToken(jwt) && jwtUtil.validateSignature(jwt)) {
                    final String cpf = jwtUtil.extractCpf(jwt);
                    if (cpf != null && cpf.length() == 11) {
                        final UserDetails clientPrincipal = User.withUsername(cpf)
                                .password("N/A")
                                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + SecurityPaths.ROLE_CLIENTE)))
                                .build();
                        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                clientPrincipal, null, clientPrincipal.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } else {
                    final String username = jwtUtil.extractUsername(jwt);
                    if (username != null) {
                        try {
                            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                            if (jwtUtil.validateToken(jwt, userDetails.getUsername()) && userDetails.isEnabled()) {
                                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities()
                                );
                                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                            }
                        } catch (UsernameNotFoundException e) {
                            // User not found, do nothing
                        }
                    }
                }
            } catch (Exception e) {
                // Invalid token, do nothing
            }
        }
        filterChain.doFilter(request, response);
    }
}
