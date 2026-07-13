package com.autoservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ValueObject")
class ValueObjectTest {

    @Test
    @DisplayName("Deve testar a instanciação da classe abstrata via classe anonima")
    void testConstructor() {

        class DummyValueObject extends ValueObject {
            public DummyValueObject() {
                super();
            }
        }

        ValueObject valueObject = new DummyValueObject();
        assertNotNull(valueObject);
    }
}
