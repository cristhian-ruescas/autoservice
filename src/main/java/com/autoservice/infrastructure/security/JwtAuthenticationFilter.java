package com.autoservice.infrastructure.security;

import com.auth0.jwt.interfaces.DecodedJWT;
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

    public static final String ROLE_CLIENTE = "ROLE_CLIENTE";

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

        if (authHeader != null && authHeader.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            final String jwt = authHeader.substring(7);
            try {
                final DecodedJWT decoded = jwtUtil.decodeToken(jwt);
                if (jwtUtil.isClienteToken(decoded)) {
                    authenticateCliente(request, jwt, decoded);
                } else {
                    authenticateAdmin(request, jwt, decoded.getSubject());
                }
            } catch (Exception ignored) {
                // Invalid token — leave unauthenticated
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateCliente(HttpServletRequest request, String jwt, DecodedJWT decoded) {
        if (!jwtUtil.validateClienteToken(jwt)) {
            return;
        }
        final String principal = decoded.getSubject();
        final UserDetails cliente = User.withUsername(principal)
                .password("N/A")
                .authorities(List.of(new SimpleGrantedAuthority(ROLE_CLIENTE)))
                .build();
        final var authToken = new UsernamePasswordAuthenticationToken(cliente, null, cliente.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void authenticateAdmin(HttpServletRequest request, String jwt, String username) {
        if (username == null) {
            return;
        }
        try {
            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails.getUsername()) && userDetails.isEnabled()) {
                final var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (UsernameNotFoundException ignored) {
            // User not found
        }
    }
}
