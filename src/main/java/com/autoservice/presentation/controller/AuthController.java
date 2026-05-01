package com.autoservice.presentation.controller;

import com.autoservice.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginData.get("username"),
                            loginData.get("password")
                    )
            );
            String token = jwtUtil.generateToken(loginData.get("username"));
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return response;
        } catch (AuthenticationException e) {
            throw new RuntimeException("Credenciais inválidas");
        }
    }
}

