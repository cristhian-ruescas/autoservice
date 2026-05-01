package com.autoservice.presentation.controller;

import com.autoservice.config.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "admin");
        loginData.put("password", "admin");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtUtil.generateToken("admin")).thenReturn("mocked-jwt-token");

        Map<String, String> response = authController.login(loginData);
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.get("token"));
    }

    @Test
    void testLoginFailure() {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("username", "admin");
        loginData.put("password", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new AuthenticationException("Invalid"){});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authController.login(loginData));
        assertEquals("Credenciais inválidas", exception.getMessage());
    }
}

