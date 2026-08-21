package com.autoservice.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import com.autoservice.infrastructure.observability.RequestCorrelationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            final JwtUtil jwtUtil,
            final UsuarioUserDetailsService usuarioUserDetailsService
    ) {
        return new JwtAuthenticationFilter(jwtUtil, usuarioUserDetailsService);
    }

    @Bean
    public RequestCorrelationFilter requestCorrelationFilter() {
        return new RequestCorrelationFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(
            final HttpSecurity http,
            final RequestCorrelationFilter requestCorrelationFilter,
            final JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(SecurityPaths.PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, SecurityPaths.ATENDIMENTO_ABERTURA)
                        .hasAnyRole(SecurityPaths.ROLE_ADMIN, SecurityPaths.ROLE_CUSTOMER)
                        .requestMatchers(HttpMethod.GET, SecurityPaths.CLIENTE_BY_CPF)
                        .hasAnyRole(SecurityPaths.ROLE_ADMIN, SecurityPaths.ROLE_CUSTOMER)
                        .requestMatchers(HttpMethod.GET, SecurityPaths.ANDAMENTO_ORDEM_SERVICO)
                        .hasAnyRole(SecurityPaths.ROLE_ADMIN, SecurityPaths.ROLE_CUSTOMER)
                        .requestMatchers(HttpMethod.GET, SecurityPaths.APROVACAO_APROVAR)
                        .hasAnyRole(SecurityPaths.ROLE_ADMIN, SecurityPaths.ROLE_CUSTOMER)
                        .requestMatchers(HttpMethod.GET, SecurityPaths.APROVACAO_REPROVAR)
                        .hasAnyRole(SecurityPaths.ROLE_ADMIN, SecurityPaths.ROLE_CUSTOMER)
                        .requestMatchers(SecurityPaths.ADMIN_ENDPOINTS).hasRole(SecurityPaths.ROLE_ADMIN)
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().write("{\"error\":\"Credenciais inválidas ou token ausente\"}");
                }))
                .addFilterBefore(requestCorrelationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, RequestCorrelationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            final HttpSecurity http,
            final PasswordEncoder encoder,
            final UsuarioUserDetailsService usuarioUserDetailsService
    ) throws Exception {
        final AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(usuarioUserDetailsService).passwordEncoder(encoder);
        return builder.build();
    }
}
