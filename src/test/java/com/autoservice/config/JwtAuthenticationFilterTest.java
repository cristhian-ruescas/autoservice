package com.autoservice.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @AfterEach
    void limpaContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void semBearerHeader_soPropagaSemAutenticar() throws Exception {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        UserDetailsService uds = mock(UserDetailsService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, uds);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void bearerValido_defineAutenticacaoNoContexto() throws Exception {
        JwtUtil jwtUtil = new JwtUtil();
        UserDetailsService uds = mock(UserDetailsService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, uds);

        UserDetails user = User.withUsername("api-user").password("p").roles("USER").build();
        String token = jwtUtil.generateToken(user);
        when(uds.loadUserByUsername("api-user")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }
}
