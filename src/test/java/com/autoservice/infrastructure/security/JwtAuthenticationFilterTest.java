package com.autoservice.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
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

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_tokenValidoDeUsuarioInterno_autenticaUsuario() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenvalido");
        when(jwtUtil.extractUsername("tokenvalido")).thenReturn("admin@email.com");
        when(jwtUtil.extractRoles("tokenvalido")).thenReturn(List.of());
        when(jwtUtil.extractIssuer("tokenvalido")).thenReturn("autoservice-auth");

        final var realUser = User.withUsername("admin@email.com")
                .password("password")
                .roles("ADMIN")
                .build();

        when(userDetailsService.loadUserByUsername("admin@email.com")).thenReturn(realUser);
        when(jwtUtil.validateToken("tokenvalido", "admin@email.com")).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_tokenInvalido_naoAutenticaUsuario() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn("Bearer tokeninvalido");
        when(jwtUtil.extractUsername("tokeninvalido")).thenReturn("admin@email.com");
        when(jwtUtil.extractRoles("tokeninvalido")).thenReturn(List.of());
        when(userDetailsService.loadUserByUsername("admin@email.com")).thenReturn(
                User.withUsername("admin@email.com").password("password").roles("ADMIN").build()
        );
        when(jwtUtil.validateToken("tokeninvalido", "admin@email.com")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_tokenLambdaDeCliente_autenticaSemBuscarUsuarioInterno() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/atendimentos");
        when(request.getHeader("Authorization")).thenReturn("Bearer tokencliente");
        when(jwtUtil.extractUsername("tokencliente")).thenReturn("39053344705");
        when(jwtUtil.extractRoles("tokencliente")).thenReturn(List.of("CUSTOMER"));
        when(jwtUtil.extractCustomerStatus("tokencliente")).thenReturn("ATIVO");
        when(jwtUtil.extractIssuer("tokencliente")).thenReturn("autoservice-auth");
        when(jwtUtil.validateToken("tokencliente", "39053344705")).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername("39053344705");
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .filter(authority -> authority.equals(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                .findFirst()
                .orElse(null));
    }
}
