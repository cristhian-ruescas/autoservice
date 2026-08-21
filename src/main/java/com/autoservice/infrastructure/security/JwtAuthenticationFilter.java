package com.autoservice.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTH_SUBJECT_MDC_KEY = "authSubject";
    private static final String AUTH_CPF_MDC_KEY = "authCpf";
    private static final String AUTH_ROLES_MDC_KEY = "authRoles";
    private static final String AUTH_ISSUER_MDC_KEY = "authIssuer";

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
        try {
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                final String jwt = authHeader.substring(7);
                authenticate(request, jwt);
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(AUTH_SUBJECT_MDC_KEY);
            MDC.remove(AUTH_CPF_MDC_KEY);
            MDC.remove(AUTH_ROLES_MDC_KEY);
            MDC.remove(AUTH_ISSUER_MDC_KEY);
            MDC.remove("customerStatus");
        }
    }

    private void authenticate(HttpServletRequest request, String jwt) {
        String username;
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            return;
        }

        if (username == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        final List<String> roles = jwtUtil.extractRoles(jwt);
        if (roles.contains(SecurityPaths.ROLE_CUSTOMER) && jwtUtil.validateToken(jwt, username)) {
            setAuthentication(
                    request,
                    username,
                    roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList(),
                    jwtUtil.extractCustomerStatus(jwt),
                    jwtUtil.extractIssuer(jwt)
            );
            return;
        }

        try {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(jwt, userDetails.getUsername()) && userDetails.isEnabled()) {
                setAuthentication(
                        request,
                        userDetails,
                        userDetails.getAuthorities(),
                        null,
                        jwtUtil.extractIssuer(jwt)
                );
            }
        } catch (UsernameNotFoundException ignored) {
            // Usuário interno não encontrado; segue sem autenticar.
        }
    }

    private void setAuthentication(
            HttpServletRequest request,
            String subject,
            Collection<? extends GrantedAuthority> authorities,
            String customerStatus,
            String issuer
    ) {
        UserDetails principal = User.withUsername(subject)
                .password("N/A")
                .authorities(authorities)
                .build();
        setAuthentication(request, principal, authorities, customerStatus, issuer);
        MDC.put(AUTH_CPF_MDC_KEY, subject);
    }

    private void setAuthentication(
            HttpServletRequest request,
            UserDetails principal,
            Collection<? extends GrantedAuthority> authorities,
            String customerStatus,
            String issuer
    ) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                principal, null, authorities
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        MDC.put(AUTH_SUBJECT_MDC_KEY, principal.getUsername());
        MDC.put(AUTH_ROLES_MDC_KEY, authorities.stream().map(GrantedAuthority::getAuthority).toList().toString());
        if (customerStatus != null) {
            MDC.put("customerStatus", customerStatus);
        }
        if (issuer != null) {
            MDC.put(AUTH_ISSUER_MDC_KEY, issuer);
        }
    }
}
