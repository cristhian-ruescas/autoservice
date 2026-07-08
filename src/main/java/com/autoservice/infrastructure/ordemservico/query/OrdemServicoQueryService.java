package com.autoservice.infrastructure.ordemservico.query;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoOutput;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoQuery;
import com.autoservice.application.ordemservico.status.ConsultarStatusOrdemServicoOutput;
import com.autoservice.application.ordemservico.status.ConsultarStatusOrdemServicoQuery;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoQuery;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoQuery;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.peca.Peca;
import com.autoservice.infrastructure.persistence.entity.ItemServicoJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PecaJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.ItemServicoMapper;
import com.autoservice.infrastructure.persistence.mapper.PecaMapper;
import com.autoservice.infrastructure.query.PaginacaoValidator;
import com.autoservice.infrastructure.query.mapper.OrdemServicoReadModelMapper;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrdemServicoQueryService implements ListOrdemServicoQuery, DetailOrdemServicoQuery, AcompanharOrdemServicoQuery, ConsultarStatusOrdemServicoQuery {

    private static final String RESUMO_ORDEM_SERVICO_QUERY = """
            select os, v, tipoVeiculo, c, pf, pj, representante
            from OrdemServicoJpaEntity os
            join VeiculoJpaEntity v on v.id = os.veiculoId
            join TipoVeiculoJpaEntity tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
            join ClienteJpaEntity c on c.pessoaId = v.proprietarioId
            left join PessoaFisicaJpaEntity pf on pf.id = c.pessoaId
            left join PessoaJuridicaJpaEntity pj on pj.id = c.pessoaId
            left join PessoaFisicaJpaEntity representante on representante.id = pj.representanteLegalId
            """;

    private final EntityManager entityManager;

    public OrdemServicoQueryService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(readOnly = true)
    public PaginationOutput<ListOrdemServicoOutput> execute(
            final int page,
            final int size,
            final String status
    ) {
        PaginacaoValidator.validar(page, size);
        final var statusNormalizado = normalizeStatus(status);

        final var query = RESUMO_ORDEM_SERVICO_QUERY + """
                where os.status not in ('FINALIZADA', 'ENTREGUE')
                  and (:status is null or os.status = :status)
                order by case os.status
                    when 'EM_EXECUCAO' then 1
                    when 'AGUARDANDO_APROVACAO' then 2
                    when 'EM_DIAGNOSTICO' then 3
                    when 'RECEBIDO' then 4
                    else 5
                end,
                os.dataCriacao asc
                """;

        final var items = this.entityManager.createQuery(query, Object[].class)
                .setParameter("status", statusNormalizado)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(OrdemServicoReadModelMapper::fromQueryRow)
                .toList();

        return PaginationOutput.from(items, page, size, totalOrdensServico(statusNormalizado));
    }

    @Override
    @Transactional(readOnly = true)
    public DetailOrdemServicoOutput execute(final UUID ordemServicoId) {
        final var id = OrdemServicoID.from(ordemServicoId);

        return DetailOrdemServicoOutput.from(buscarResumo(id), buscarItens(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AcompanharOrdemServicoOutput acompanhar(final UUID ordemServicoId) {
        final var id = OrdemServicoID.from(ordemServicoId);
        final var itens = buscarItens(id).stream()
                .map(item -> new AcompanharOrdemServicoOutput.ItemOutput(
                        item.tipo(),
                        item.descricao(),
                        item.peca() == null ? null : item.peca().codigo(),
                        item.quantidade(),
                        item.valorUnitario(),
                        item.valorTotal()
                ))
                .toList();

        return AcompanharOrdemServicoOutput.from(buscarResumo(id), itens);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultarStatusOrdemServicoOutput consultar(final UUID ordemServicoId) {
        return ConsultarStatusOrdemServicoOutput.from(buscarResumo(OrdemServicoID.from(ordemServicoId)));
    }

    private ListOrdemServicoOutput buscarResumo(final OrdemServicoID id) {
        final var query = RESUMO_ORDEM_SERVICO_QUERY + " where os.id = :id";

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("id", id.getValue())
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Ordem de serviço não encontrada"));
        }

        return OrdemServicoReadModelMapper.fromQueryRow(rows.getFirst());
    }

    private List<DetailOrdemServicoOutput.ItemOutput> buscarItens(final OrdemServicoID id) {
        final var query = """
                select item, peca
                from ItemServicoJpaEntity item
                left join PecaJpaEntity peca on peca.id = item.pecaId
                where item.ordemServicoId = :id
                order by item.tipo asc, item.descricao asc
                """;

        return this.entityManager.createQuery(query, Object[].class)
                .setParameter("id", id.getValue())
                .getResultList()
                .stream()
                .map(this::mapItem)
                .toList();
    }

    private DetailOrdemServicoOutput.ItemOutput mapItem(final Object[] row) {
        final ItemServico item = ItemServicoMapper.toDomain((ItemServicoJpaEntity) row[0]);
        final Peca peca = row[1] == null ? null : PecaMapper.toDomain((PecaJpaEntity) row[1]);

        return new DetailOrdemServicoOutput.ItemOutput(
                item.getId().getValue(),
                item.getTipo().name(),
                item.getDescricao(),
                item.getPecaId() == null ? null : item.getPecaId().getValue(),
                mapPeca(peca),
                item.getQuantidade(),
                item.getValorUnitario(),
                item.getValorTotal()
        );
    }

    private DetailOrdemServicoOutput.PecaOutput mapPeca(final Peca peca) {
        if (peca == null) {
            return null;
        }

        return new DetailOrdemServicoOutput.PecaOutput(
                peca.getId().getValue(),
                peca.getCodigo(),
                peca.getDescricao(),
                peca.getTipoVeiculoId() == null ? null : peca.getTipoVeiculoId().getValue()
        );
    }

    private long totalOrdensServico(final OrdemServicoStatus status) {
        final var query = """
                select count(os)
                from OrdemServicoJpaEntity os
                where os.status not in ('FINALIZADA', 'ENTREGUE')
                  and (:status is null or os.status = :status)
                """;

        return this.entityManager.createQuery(query, Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    private OrdemServicoStatus normalizeStatus(final String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return OrdemServicoStatus.valueOf(status.trim().toUpperCase());
        } catch (final IllegalArgumentException ex) {
            throw DomainException.with(new Error("Status da ordem de serviço inválido"));
        }
    }
}
