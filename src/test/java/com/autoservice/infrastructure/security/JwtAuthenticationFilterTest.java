package com.autoservice.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_tokenValido_autenticaUsuario() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenvalido");
        final var decoded = org.mockito.Mockito.mock(com.auth0.jwt.interfaces.DecodedJWT.class);
        when(jwtUtil.decodeToken("tokenvalido")).thenReturn(decoded);
        when(jwtUtil.isClienteToken(decoded)).thenReturn(false);
        when(decoded.getSubject()).thenReturn("admin@email.com");
        org.springframework.security.core.userdetails.User realUser =
                new org.springframework.security.core.userdetails.User(
                        "admin@email.com",
                        "password",
                        java.util.Collections.singletonList(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );
        when(userDetailsService.loadUserByUsername("admin@email.com")).thenReturn(realUser);
        when(jwtUtil.validateToken("tokenvalido", "admin@email.com")).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_tokenCliente_autenticaComRoleCliente() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer tokencliente");
        final var decoded = org.mockito.Mockito.mock(com.auth0.jwt.interfaces.DecodedJWT.class);
        when(jwtUtil.decodeToken("tokencliente")).thenReturn(decoded);
        when(jwtUtil.isClienteToken(decoded)).thenReturn(true);
        when(jwtUtil.validateClienteToken("tokencliente")).thenReturn(true);
        when(decoded.getSubject()).thenReturn("cliente-uuid");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(JwtAuthenticationFilter.ROLE_CLIENTE)));
    }

    @Test
    void doFilterInternal_tokenInvalido_naoAutenticaUsuario() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer tokeninvalido");
        when(jwtUtil.decodeToken("tokeninvalido")).thenThrow(new RuntimeException("invalid"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
