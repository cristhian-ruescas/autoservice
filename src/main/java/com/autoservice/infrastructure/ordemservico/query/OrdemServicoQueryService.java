package com.autoservice.infrastructure.ordemservico.query;

import com.autoservice.application.PaginationOutput;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoOutput;
import com.autoservice.application.ordemservico.acompanhamento.AcompanharOrdemServicoQuery;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoOutput;
import com.autoservice.application.ordemservico.detail.DetailOrdemServicoQuery;
import com.autoservice.application.ordemservico.list.ListOrdemServicoOutput;
import com.autoservice.application.ordemservico.list.ListOrdemServicoQuery;
import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.pessoa.PessoaFisica;
import com.autoservice.domain.pessoa.PessoaJuridica;
import com.autoservice.domain.tipoveiculo.TipoVeiculo;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.validation.Error;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrdemServicoQueryService implements ListOrdemServicoQuery, DetailOrdemServicoQuery, AcompanharOrdemServicoQuery {

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
        validarPaginacao(page, size);
        final var statusNormalizado = normalizeStatus(status);

        final var query = """
                select os, v, tipoVeiculo, c, pf, pj, representante
                from OrdemServico os
                join Veiculo v on v.embeddedId = os.veiculoId
                join TipoVeiculo tipoVeiculo on tipoVeiculo.embeddedId = v.tipoVeiculoId
                join Cliente c on c.pessoaId = v.proprietarioId
                left join PessoaFisica pf on pf.embeddedId = c.pessoaId
                left join PessoaJuridica pj on pj.embeddedId = c.pessoaId
                left join PessoaFisica representante on representante.embeddedId = pj.representanteLegalId
                where (:status is null or os.status = :status)
                order by os.dataCriacao.value desc
                """;

        final var items = this.entityManager.createQuery(query, Object[].class)
                .setParameter("status", statusNormalizado)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList()
                .stream()
                .map(this::mapToOutput)
                .toList();

        return PaginationOutput.from(items, page, size, totalOrdensServico(statusNormalizado));
    }

    @Override
    @Transactional(readOnly = true)
    public DetailOrdemServicoOutput execute(final UUID ordemServicoId) {
        final var id = OrdemServicoID.from(ordemServicoId);

        final var ordemServico = buscarResumo(id);
        final var itens = buscarItens(id);

        return DetailOrdemServicoOutput.from(ordemServico, itens);
    }

    @Override
    @Transactional(readOnly = true)
    public AcompanharOrdemServicoOutput acompanhar(final UUID ordemServicoId) {
        final var id = OrdemServicoID.from(ordemServicoId);

        return AcompanharOrdemServicoOutput.from(buscarResumo(id));
    }

    private ListOrdemServicoOutput buscarResumo(final OrdemServicoID id) {
        final var query = """
                select os, v, tipoVeiculo, c, pf, pj, representante
                from OrdemServico os
                join Veiculo v on v.embeddedId = os.veiculoId
                join TipoVeiculo tipoVeiculo on tipoVeiculo.embeddedId = v.tipoVeiculoId
                join Cliente c on c.pessoaId = v.proprietarioId
                left join PessoaFisica pf on pf.embeddedId = c.pessoaId
                left join PessoaJuridica pj on pj.embeddedId = c.pessoaId
                left join PessoaFisica representante on representante.embeddedId = pj.representanteLegalId
                where os.embeddedId = :id
                """;

        final var rows = this.entityManager.createQuery(query, Object[].class)
                .setParameter("id", id)
                .getResultList();

        if (rows.isEmpty()) {
            throw DomainException.with(new Error("Ordem de serviço não encontrada"));
        }

        return this.mapToOutput(rows.getFirst());
    }

    private List<DetailOrdemServicoOutput.ItemOutput> buscarItens(final OrdemServicoID id) {
        final var query = """
                select item, peca
                from ItemServico item
                left join Peca peca on peca.embeddedId = item.pecaId
                where item.ordemServicoId = :id
                order by item.tipo asc, item.descricao asc
                """;

        return this.entityManager.createQuery(query, Object[].class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .map(this::mapItem)
                .toList();
    }

    private DetailOrdemServicoOutput.ItemOutput mapItem(final Object[] row) {
        final var item = (ItemServico) row[0];
        final var peca = (Peca) row[1];

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

    private ListOrdemServicoOutput mapToOutput(final Object[] row) {
        final var ordemServico = (OrdemServico) row[0];
        final var veiculo = (Veiculo) row[1];
        final var tipoVeiculo = (TipoVeiculo) row[2];
        final var cliente = (Cliente) row[3];
        final var pessoaFisica = (PessoaFisica) row[4];
        final var pessoaJuridica = (PessoaJuridica) row[5];
        final var representante = (PessoaFisica) row[6];

        return new ListOrdemServicoOutput(
                ordemServico.getId().getValue(),
                ordemServico.getStatus().name(),
                ordemServico.getDataCriacao().getValue(),
                ordemServico.getRelato(),
                ordemServico.getTempoPrevistoExecucaoDias(),
                ordemServico.getTempoPrevistoExecucaoHoras(),
                ordemServico.getIniciadoEm(),
                ordemServico.getFinalizadoEm(),
                mapVeiculo(veiculo, tipoVeiculo),
                mapCliente(cliente, pessoaFisica, pessoaJuridica, representante)
        );
    }

    private long totalOrdensServico(final OrdemServicoStatus status) {
        final var query = """
                select count(os)
                from OrdemServico os
                where (:status is null or os.status = :status)
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

    private void validarPaginacao(final int page, final int size) {
        if (page < 0) {
            throw DomainException.with(new Error("Página não deve ser menor que zero"));
        }
        if (size <= 0) {
            throw DomainException.with(new Error("Tamanho da página deve ser maior que zero"));
        }
        if (size > 100) {
            throw DomainException.with(new Error("Tamanho da página não deve ser maior que 100"));
        }
    }

    private ListOrdemServicoOutput.VeiculoOutput mapVeiculo(
            final Veiculo veiculo,
            final TipoVeiculo tipoVeiculo
    ) {
        return new ListOrdemServicoOutput.VeiculoOutput(
                veiculo.getId().getValue(),
                veiculo.getPlaca().getValue(),
                tipoVeiculo.getMarca().getValue(),
                tipoVeiculo.getModelo().getValue(),
                tipoVeiculo.getAno().getValue(),
                veiculo.getCor().getValue(),
                veiculo.getKilometragem().getValue()
        );
    }

    private ListOrdemServicoOutput.ClienteOutput mapCliente(
            final Cliente cliente,
            final PessoaFisica pessoaFisica,
            final PessoaJuridica pessoaJuridica,
            final PessoaFisica representante
    ) {
        if (pessoaJuridica != null) {
            return new ListOrdemServicoOutput.ClienteOutput(
                    cliente.getId().getValue(),
                    "JURIDICA",
                    null,
                    null,
                    pessoaJuridica.getRazaoSocial(),
                    pessoaJuridica.getCnpj().getValue(),
                    valueOf(pessoaJuridica.getEmail()),
                    valueOf(pessoaJuridica.getTelefone()),
                    mapRepresentanteLegal(representante)
            );
        }

        return new ListOrdemServicoOutput.ClienteOutput(
                cliente.getId().getValue(),
                "FISICA",
                pessoaFisica.getNome(),
                pessoaFisica.getCpf().getValue(),
                null,
                null,
                valueOf(pessoaFisica.getEmail()),
                valueOf(pessoaFisica.getTelefone()),
                null
        );
    }

    private ListOrdemServicoOutput.RepresentanteLegalOutput mapRepresentanteLegal(
            final PessoaFisica representante
    ) {
        if (representante == null) {
            return null;
        }

        return new ListOrdemServicoOutput.RepresentanteLegalOutput(
                representante.getNome(),
                representante.getCpf().getValue(),
                valueOf(representante.getEmail()),
                valueOf(representante.getTelefone())
        );
    }

    private String valueOf(final Object valueObject) {
        return valueObject == null ? null : valueObject.toString();
    }
}
