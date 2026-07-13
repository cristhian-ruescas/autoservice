package com.autoservice.infrastructure.ordemservico.metricas;

import com.autoservice.application.ordemservico.metricas.TempoMedioExecucaoOutput;
import com.autoservice.application.ordemservico.metricas.TempoMedioExecucaoQuery;
import com.autoservice.application.ordemservico.metricas.TempoMedioPorDescricaoItemOutput;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TempoMedioExecucaoQueryService implements TempoMedioExecucaoQuery {

    private final EntityManager entityManager;

    public TempoMedioExecucaoQueryService(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    private static Double toDouble(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof final BigDecimal bd) {
            return bd.doubleValue();
        }
        if (value instanceof final Double d) {
            return d;
        }
        if (value instanceof final Number n) {
            return n.doubleValue();
        }
        return null;
    }

    private static Double segundosParaHoras(final Double segundos) {
        if (segundos == null) {
            return null;
        }
        return segundos / 3600.0;
    }

    @Override
    @Transactional(readOnly = true)
    public TempoMedioExecucaoOutput consultar() {
        final Double globalSegundos = mediaGlobalSegundos();
        final List<TempoMedioPorDescricaoItemOutput> porDescricao = mediasPorDescricaoItem();

        return new TempoMedioExecucaoOutput(
                globalSegundos,
                segundosParaHoras(globalSegundos),
                porDescricao
        );
    }

    private Double mediaGlobalSegundos() {
        final var sql = """
                select avg(extract(epoch from (os.finalizado_em - os.iniciado_em)))
                from servico.ordem_servico os
                where os.status in ('FINALIZADA', 'ENTREGUE')
                  and os.iniciado_em is not null
                  and os.finalizado_em is not null
                """;

        final var result = this.entityManager.createNativeQuery(sql).getSingleResult();
        if (result == null) {
            return null;
        }
        if (result instanceof final BigDecimal bd) {
            return bd.doubleValue();
        }
        if (result instanceof final Double d) {
            return d;
        }
        if (result instanceof final Number n) {
            return n.doubleValue();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<TempoMedioPorDescricaoItemOutput> mediasPorDescricaoItem() {
        final var sql = """
                select i.descricao,
                       avg(extract(epoch from (os.finalizado_em - os.iniciado_em))),
                       count(distinct os.id)
                from servico.item_servico i
                join servico.ordem_servico os on os.id = i.ordem_servico_id
                where i.tipo = 'SERVICO'
                  and os.status in ('FINALIZADA', 'ENTREGUE')
                  and os.iniciado_em is not null
                  and os.finalizado_em is not null
                group by i.descricao
                order by i.descricao
                """;

        final var rows = this.entityManager.createNativeQuery(sql).getResultList();
        final var list = new ArrayList<TempoMedioPorDescricaoItemOutput>();
        for (final Object row : rows) {
            final Object[] cols = (Object[]) row;
            final String descricao = Objects.toString(cols[0], null);
            final Double segundos = toDouble(cols[1]);
            final Long qtd = cols[2] instanceof final Number n ? n.longValue() : null;
            list.add(new TempoMedioPorDescricaoItemOutput(
                    descricao,
                    segundos,
                    segundosParaHoras(segundos),
                    qtd
            ));
        }
        return list;
    }
}
