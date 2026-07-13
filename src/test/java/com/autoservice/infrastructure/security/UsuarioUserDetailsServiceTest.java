package com.autoservice.infrastructure.security;

import com.autoservice.domain.usuario.Usuario;
import com.autoservice.domain.usuario.UsuarioID;
import com.autoservice.infrastructure.persistence.mapper.UsuarioMapper;
import com.autoservice.infrastructure.usuario.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class UsuarioUserDetailsServiceTest {

    @Mock
    private UsuarioJpaRepository usuarioRepository;

    @InjectMocks
    private UsuarioUserDetailsService usuarioUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_usuarioExiste_retornaUserDetails() {
        Usuario usuario = new Usuario(UsuarioID.unique(), "admin@autoservice.local", "senhaCriptografada", "ADMIN");
        when(usuarioRepository.findByEmail("admin@autoservice.local")).thenReturn(Optional.of(UsuarioMapper.toEntity(usuario)));

        UserDetails userDetails = usuarioUserDetailsService.loadUserByUsername("admin@autoservice.local");
        assertEquals("admin@autoservice.local", userDetails.getUsername());
        assertEquals("senhaCriptografada", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_usuarioNaoExiste_lancaExcecao() {
        when(usuarioRepository.findByEmail("naoexiste@autoservice.local")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () ->
                usuarioUserDetailsService.loadUserByUsername("naoexiste@autoservice.local")
        );
    }
}
