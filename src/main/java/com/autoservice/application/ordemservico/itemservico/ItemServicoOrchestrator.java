package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class ItemServicoOrchestrator {

    private final OrdemServicoGateway ordemServicoGateway;
    private final PecaGateway pecaGateway;

    public ItemServicoOrchestrator(
            final OrdemServicoGateway ordemServicoGateway,
            final PecaGateway pecaGateway
    ) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.pecaGateway = Objects.requireNonNull(pecaGateway);
    }

    public OrdemServico exigirOrdemEmDiagnostico(final OrdemServicoID ordemServicoId) {
        final var ordemServico = this.ordemServicoGateway.findById(ordemServicoId)
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));

        if (ordemServico.getStatus() != OrdemServicoStatus.EM_DIAGNOSTICO) {
            throw DomainException.with(new Error(
                    "Itens de serviço e peças só podem ser alterados quando a ordem estiver EM_DIAGNOSTICO"
            ));
        }

        return ordemServico;
    }

    public ItemServico criar(final AdicionarItemServicoCommand command, final OrdemServicoID ordemServicoId) {
        if (command == null) {
            throw DomainException.with(new Error("Item de serviço não deve ser nulo"));
        }
        if (command.tipo() == null) {
            throw DomainException.with(new Error("Tipo do item de serviço não deve ser nulo"));
        }

        return command.tipo() == ItemServicoTipo.PECA
                ? criarItemPeca(command, ordemServicoId)
                : criarItemServico(command, ordemServicoId);
    }

    public ItemServico mesclar(
            final AtualizarItemServicoCommand command,
            final ItemServico itemAtual,
            final OrdemServicoID ordemServicoId
    ) {
        final var tipo = command.tipo() == null ? itemAtual.getTipo() : command.tipo();

        return tipo == ItemServicoTipo.PECA
                ? mesclarItemPeca(command, itemAtual, ordemServicoId)
                : mesclarItemServico(command, itemAtual, ordemServicoId);
    }

    private ItemServico criarItemServico(
            final AdicionarItemServicoCommand command,
            final OrdemServicoID ordemServicoId
    ) {
        return ItemServico.newServico(
                ordemServicoId,
                command.descricao(),
                command.valorUnitario()
        );
    }

    private ItemServico criarItemPeca(
            final AdicionarItemServicoCommand command,
            final OrdemServicoID ordemServicoId
    ) {
        if (command.pecaId() == null) {
            throw DomainException.with(new Error("Peça é obrigatória para item do tipo PECA"));
        }

        final Peca peca = buscarPeca(command.pecaId());

        return ItemServico.newPeca(
                ordemServicoId,
                descricaoPeca(command.descricao(), peca),
                peca.getId(),
                command.quantidade(),
                peca.getValorUnitario()
        );
    }

    private ItemServico mesclarItemServico(
            final AtualizarItemServicoCommand command,
            final ItemServico itemAtual,
            final OrdemServicoID ordemServicoId
    ) {
        return ItemServico.with(
                itemAtual.getId(),
                ordemServicoId,
                ItemServicoTipo.SERVICO,
                command.descricao() == null ? itemAtual.getDescricao() : command.descricao(),
                null,
                1,
                command.valorUnitario() == null ? itemAtual.getValorUnitario() : command.valorUnitario()
        );
    }

    private ItemServico mesclarItemPeca(
            final AtualizarItemServicoCommand command,
            final ItemServico itemAtual,
            final OrdemServicoID ordemServicoId
    ) {
        final var peca = obterPeca(command, itemAtual);

        return ItemServico.with(
                itemAtual.getId(),
                ordemServicoId,
                ItemServicoTipo.PECA,
                resolverDescricaoPeca(command, itemAtual, peca),
                peca.getId(),
                command.quantidade() == null ? itemAtual.getQuantidade() : command.quantidade(),
                peca.getValorUnitario()
        );
    }

    private Peca obterPeca(final AtualizarItemServicoCommand command, final ItemServico itemAtual) {
        if (command.pecaId() != null) {
            return buscarPeca(command.pecaId());
        }

        if (itemAtual.getPecaId() == null) {
            throw DomainException.with(new Error("Peça é obrigatória para item do tipo PECA"));
        }

        return this.pecaGateway.findById(itemAtual.getPecaId())
                .orElseThrow(() -> DomainException.with(new Error("Peça não encontrada")));
    }

    private Peca buscarPeca(final java.util.UUID pecaId) {
        return this.pecaGateway.findById(PecaID.from(pecaId))
                .orElseThrow(() -> DomainException.with(new Error("Peça não encontrada")));
    }

    private String descricaoPeca(final String descricao, final Peca peca) {
        return descricao == null || descricao.isBlank() ? peca.getDescricao() : descricao;
    }

    private String resolverDescricaoPeca(
            final AtualizarItemServicoCommand command,
            final ItemServico itemAtual,
            final Peca peca
    ) {
        if (command.descricao() != null && !command.descricao().isBlank()) {
            return command.descricao();
        }
        if (command.descricao() != null) {
            return peca.getDescricao();
        }
        if (itemAtual.getTipo() == ItemServicoTipo.PECA) {
            return itemAtual.getDescricao();
        }

        return peca.getDescricao();
    }
}
