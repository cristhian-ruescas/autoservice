package com.autoservice.domain.auth;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    @Test
    void construtorPadraoParaJpa() {
        User u = new User();
        assertNull(u.getId());
        assertNull(u.getUsername());
        assertNull(u.getPassword());
        assertNull(u.getRoles());
    }

    @Test
    void construtorComArgumentosESetters() {
        User u = new User("u1", "p1", Set.of("A", "B"));
        assertEquals("u1", u.getUsername());
        assertEquals("p1", u.getPassword());
        assertEquals(Set.of("A", "B"), u.getRoles());

        u.setId(10L);
        u.setUsername("u2");
        u.setPassword("p2");
        u.setRoles(Set.of("X"));
        assertEquals(10L, u.getId());
        assertEquals("u2", u.getUsername());
        assertEquals("p2", u.getPassword());
        assertEquals(Set.of("X"), u.getRoles());
    }
}
