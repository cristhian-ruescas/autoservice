package com.autoservice.domain;

import com.autoservice.validation.ValidationHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("AggregateRoot")
class AggregateRootTest {

    @Test
    @DisplayName("Deve garantir a cobertura do construtor e super do AggregateRoot")
    void testAggregateRootConstructor() {
        
        Identifier id = new Identifier() {
            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };

        class DummyAggregateRoot extends AggregateRoot<Identifier> {
            
            public DummyAggregateRoot() {
                super();
            }

            public DummyAggregateRoot(Identifier id) {
                super(id);
            }

            @Override
            public void validate(ValidationHandler handler) {
            }

            @Override
            public Identifier getId() {
                return this.id;
            }
        }

        DummyAggregateRoot aggregate = new DummyAggregateRoot(id);
        DummyAggregateRoot aggregateEmpty = new DummyAggregateRoot();

        assertNotNull(aggregate);
        assertNotNull(aggregateEmpty);
    }
}
