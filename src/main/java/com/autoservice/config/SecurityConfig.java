package com.autoservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/ordens-servico/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/tipos-veiculo/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/clientes/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/veiculos/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/pecas/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/estoque/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/clientes/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/veiculos/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/pecas/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.POST, "/estoque/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PUT, "/clientes/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PUT, "/veiculos/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PUT, "/pecas/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PUT, "/estoque/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/clientes/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/veiculos/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/pecas/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/estoque/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/clientes/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/veiculos/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/pecas/**").authenticated()
            .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/estoque/**").authenticated()
            .anyRequest().authenticated()
        );
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
