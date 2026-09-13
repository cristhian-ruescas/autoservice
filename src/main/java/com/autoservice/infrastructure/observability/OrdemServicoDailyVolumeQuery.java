package com.autoservice.infrastructure.observability;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Objects;

@Repository
public class OrdemServicoDailyVolumeQuery {

    private final EntityManager entityManager;

    public OrdemServicoDailyVolumeQuery(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    public long contarAbertasHoje() {
        final var sql = """
                select count(*)
                from servico.ordem_servico os
                where os.data_criacao >= current_date
                """;

        final var result = entityManager.createNativeQuery(sql).getSingleResult();
        if (result instanceof BigDecimal bd) {
            return bd.longValue();
        }
        if (result instanceof Number number) {
            return number.longValue();
        }
        return 0L;
    }
}
