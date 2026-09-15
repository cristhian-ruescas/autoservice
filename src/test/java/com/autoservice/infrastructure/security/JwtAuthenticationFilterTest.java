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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        when(jwtUtil.isClientToken("tokenvalido")).thenReturn(false);
        when(jwtUtil.extractUsername("tokenvalido")).thenReturn("admin@email.com");
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
        when(request.getServletPath()).thenReturn("/ordens-servico/1/andamento");
        when(request.getHeader("Authorization")).thenReturn("Bearer tokencliente");
        when(jwtUtil.isClientToken("tokencliente")).thenReturn(true);
        when(jwtUtil.validateSignature("tokencliente")).thenReturn(true);
        when(jwtUtil.extractCpf("tokencliente")).thenReturn("52998224725");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("52998224725", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_tokenInvalido_naoAutenticaUsuario() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer tokeninvalido");
        when(jwtUtil.isClientToken("tokeninvalido")).thenReturn(false);
        when(jwtUtil.extractUsername("tokeninvalido")).thenReturn("admin@email.com");
        when(userDetailsService.loadUserByUsername("admin@email.com")).thenReturn(userDetails);
        when(jwtUtil.validateToken("tokeninvalido", "admin@email.com")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
