package com.autoservice.application;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UnitUseCaseTest {

    @Test
    void executeChamaImplementacao() {
        final AtomicBoolean done = new AtomicBoolean(false);
        final UnitUseCase<String> uc = new UnitUseCase<>() {
            @Override
            public void execute(final String anIn) {
                done.set("in".equals(anIn));
            }
        };
        uc.execute("in");
        assertTrue(done.get());
    }
}
