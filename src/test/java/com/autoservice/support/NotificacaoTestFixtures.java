package com.autoservice.support;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;

public final class NotificacaoTestFixtures {

    public static final Instant OCORRIDO_EM = Instant.parse("2026-07-06T22:00:00Z");
    public static final LocalDate DATA_REFERENCIA = LocalDate.of(2026, Month.JULY, 6);

    private NotificacaoTestFixtures() {
    }
}
