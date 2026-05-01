package com.autoservice.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Exemplo fixo. Substitua por busca em banco de dados ou outro mecanismo.
        if ("admin".equals(username)) {
            return User.withUsername("admin")
                    .password("$2a$10$7QJ6Qw8Qw8Qw8Qw8Qw8QwOQw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8Qw8Q") // senha: admin
                    .roles("ADMIN")
                    .build();
        }
        throw new UsernameNotFoundException("Usuário não encontrado");
    }
}

