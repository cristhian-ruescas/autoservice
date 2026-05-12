package com.autoservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Identifier")
class IdentifierTest {

    @Test
    @DisplayName("Deve testar a instanciação da classe abstrata via classe anonima")
    void testConstructor() {

        class DummyIdentifier extends Identifier {
            public DummyIdentifier() {
                super();
            }
        }

        Identifier identifier = new DummyIdentifier();
        assertNotNull(identifier);
    }
}
