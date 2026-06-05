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
import com.autoservice.infrastructure.persistence.entity.ClienteJpaEntity;
import com.autoservice.infrastructure.persistence.entity.ItemServicoJpaEntity;
import com.autoservice.infrastructure.persistence.entity.OrdemServicoJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PecaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PessoaFisicaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.PessoaJuridicaJpaEntity;
import com.autoservice.infrastructure.persistence.entity.TipoVeiculoJpaEntity;
import com.autoservice.infrastructure.persistence.entity.VeiculoJpaEntity;
import com.autoservice.infrastructure.persistence.mapper.ClienteMapper;
import com.autoservice.infrastructure.persistence.mapper.ItemServicoMapper;
import com.autoservice.infrastructure.persistence.mapper.OrdemServicoMapper;
import com.autoservice.infrastructure.persistence.mapper.PecaMapper;
import com.autoservice.infrastructure.persistence.mapper.PessoaMapper;
import com.autoservice.infrastructure.persistence.mapper.TipoVeiculoMapper;
import com.autoservice.infrastructure.persistence.mapper.VeiculoMapper;
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
                from OrdemServicoJpaEntity os
                join VeiculoJpaEntity v on v.id = os.veiculoId
                join TipoVeiculoJpaEntity tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                join ClienteJpaEntity c on c.pessoaId = v.proprietarioId
                left join PessoaFisicaJpaEntity pf on pf.id = c.pessoaId
                left join PessoaJuridicaJpaEntity pj on pj.id = c.pessoaId
                left join PessoaFisicaJpaEntity representante on representante.id = pj.representanteLegalId
                where (:status is null or os.status = :status)
                order by os.dataCriacao desc
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
                from OrdemServicoJpaEntity os
                join VeiculoJpaEntity v on v.id = os.veiculoId
                join TipoVeiculoJpaEntity tipoVeiculo on tipoVeiculo.id = v.tipoVeiculoId
                join ClienteJpaEntity c on c.pessoaId = v.proprietarioId
                left join PessoaFisicaJpaEntity pf on pf.id = c.pessoaId
                left join PessoaJuridicaJpaEntity pj on pj.id = c.pessoaId
                left join PessoaFisicaJpaEntity representante on representante.id = pj.representanteLegalId
                where os.id = :id
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
                from ItemServicoJpaEntity item
                left join PecaJpaEntity peca on peca.id = item.pecaId
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
        final var item = ItemServicoMapper.toDomain((ItemServicoJpaEntity) row[0]);
        final var peca = row[1] == null ? null : PecaMapper.toDomain((PecaJpaEntity) row[1]);

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
        final var ordemServico = OrdemServicoMapper.toDomain((OrdemServicoJpaEntity) row[0]);
        final var veiculo = VeiculoMapper.toDomain((VeiculoJpaEntity) row[1]);
        final var tipoVeiculo = TipoVeiculoMapper.toDomain((TipoVeiculoJpaEntity) row[2]);
        final var cliente = ClienteMapper.toDomain((ClienteJpaEntity) row[3]);
        final var pessoaFisica = row[4] == null ? null : (PessoaFisica) PessoaMapper.toDomain((PessoaFisicaJpaEntity) row[4]);
        final var pessoaJuridica = row[5] == null ? null : (PessoaJuridica) PessoaMapper.toDomain((PessoaJuridicaJpaEntity) row[5]);
        final var representante = row[6] == null ? null : (PessoaFisica) PessoaMapper.toDomain((PessoaFisicaJpaEntity) row[6]);

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
                from OrdemServicoJpaEntity os
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
