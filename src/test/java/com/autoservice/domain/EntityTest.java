package com.autoservice.domain;

import com.autoservice.validation.ValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entity")
class EntityTest {

    @Test
    @DisplayName("Deve garantir que o construtor vazio seja chamado e validar equals/hashcode")
    void testEntityConstructorAndMethods() {

        Identifier id1 = new Identifier() {
            @Override
            public int hashCode() {
                return 1;
            }

            @Override
            public boolean equals(Object obj) {
                return this == obj;
            }
        };
        Identifier id2 = new Identifier() {
             @Override
            public int hashCode() {
                return 2;
            }

            @Override
            public boolean equals(Object obj) {
                return this == obj;
            }
        };

        class DummyEntity extends Entity<Identifier> {
            public DummyEntity() {
                super();
            }

            public DummyEntity(Identifier id) {
                super(id);
            }

            @Override
            public void validate(ValidationHandler handler) {

            }
        }

        DummyEntity e1 = new DummyEntity(id1);
        DummyEntity e2 = new DummyEntity(id1);
        DummyEntity e3 = new DummyEntity(id2);
        DummyEntity e4 = new DummyEntity();

        Exception exception = assertThrows(NullPointerException.class, () -> new DummyEntity(null));
        assertEquals("ID cannot be null", exception.getMessage());

        assertEquals(id1, e1.getId());
        assertNull(e4.getId());

        assertEquals(e1, e2);
        assertNotEquals(null, e1);
        assertNotEquals(e1, e3);
        assertNotEquals(e1, e4);

        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1.hashCode(), e3.hashCode());
    }
}
