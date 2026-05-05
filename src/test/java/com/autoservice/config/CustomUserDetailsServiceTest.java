package com.autoservice.config;

import com.autoservice.domain.auth.User;
import com.autoservice.domain.auth.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_mapeiaRoles() {
        var domain = new User("alice", "hash", Set.of("ROLE_ADMIN", "ROLE_USER"));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(domain));

        UserDetailsService uds = new CustomUserDetailsService(userRepository).userDetailsService();
        UserDetails details = uds.loadUserByUsername("alice");

        assertEquals("alice", details.getUsername());
        assertEquals("hash", details.getPassword());
        assertEquals(2, details.getAuthorities().size());
    }

    @Test
    void loadUserByUsername_inexistente() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        UserDetailsService uds = new CustomUserDetailsService(userRepository).userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> uds.loadUserByUsername("ghost"));
    }

    @Test
    void passwordEncoder_bcryptValidaSenha() {
        PasswordEncoder enc = new CustomUserDetailsService(userRepository).passwordEncoder();
        String hash = enc.encode("minhaSenha");
        assertTrue(enc.matches("minhaSenha", hash));
    }
}
