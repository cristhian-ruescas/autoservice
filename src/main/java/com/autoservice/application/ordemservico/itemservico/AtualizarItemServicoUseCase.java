package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.application.UseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.itemservico.ItemServicoID;
import com.autoservice.domain.itemservico.enums.ItemServicoTipo;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.peca.PecaID;
import com.autoservice.validation.Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AtualizarItemServicoUseCase extends UseCase<AtualizarItemServicoCommand, AdicionarItemServicoOutput> {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ItemServicoGateway itemServicoGateway;
    private final PecaGateway pecaGateway;

    public AtualizarItemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final ItemServicoGateway itemServicoGateway,
            final PecaGateway pecaGateway
    ) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.itemServicoGateway = Objects.requireNonNull(itemServicoGateway);
        this.pecaGateway = Objects.requireNonNull(pecaGateway);
    }

    @Override
    @Transactional
    public AdicionarItemServicoOutput execute(final AtualizarItemServicoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para atualizar item de serviço não deve ser nulo"));
        }
        if (command.ordemServicoId() == null) {
            throw DomainException.with(new Error("Ordem de serviço é obrigatória para atualizar item"));
        }
        if (command.itemServicoId() == null) {
            throw DomainException.with(new Error("Item de serviço é obrigatório para atualização"));
        }

        final var ordemServicoId = OrdemServicoID.from(command.ordemServicoId());
        final var ordemServico = this.ordemServicoGateway.findById(ordemServicoId)
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));

        if (ordemServico.getStatus() != OrdemServicoStatus.EM_DIAGNOSTICO) {
            throw DomainException.with(new Error(
                    "Itens de serviço e peças só podem ser atualizados quando a ordem estiver EM_DIAGNOSTICO"
            ));
        }

        final var itemAtual = this.itemServicoGateway.findById(ItemServicoID.from(command.itemServicoId()))
                .orElseThrow(() -> DomainException.with(new Error("Item de serviço não encontrado")));

        if (!itemAtual.getOrdemServicoId().equals(ordemServicoId)) {
            throw DomainException.with(new Error("Item de serviço não pertence à ordem de serviço informada"));
        }

        final var tipo = command.tipo() == null ? itemAtual.getTipo() : command.tipo();
        final var itemAtualizado = tipo == ItemServicoTipo.PECA
                ? atualizarItemPeca(command, itemAtual, ordemServicoId)
                : atualizarItemServico(command, itemAtual, ordemServicoId);

        return AdicionarItemServicoOutput.from(this.itemServicoGateway.update(itemAtualizado));
    }

    private ItemServico atualizarItemServico(
            final AtualizarItemServicoCommand command,
            final ItemServico itemAtual,
            final OrdemServicoID ordemServicoId
    ) {
        final var descricao = command.descricao() == null ? itemAtual.getDescricao() : command.descricao();
        final var valorUnitario = command.valorUnitario() == null
                ? itemAtual.getValorUnitario()
                : command.valorUnitario();

        return ItemServico.with(
                itemAtual.getId(),
                ordemServicoId,
                ItemServicoTipo.SERVICO,
                descricao,
                null,
                1,
                valorUnitario
        );
    }

    private ItemServico atualizarItemPeca(
            final AtualizarItemServicoCommand command,
            final ItemServico itemAtual,
            final OrdemServicoID ordemServicoId
    ) {
        final var peca = obterPeca(command, itemAtual);
        final var descricao = obterDescricaoPeca(command, itemAtual, peca);
        final var quantidade = command.quantidade() == null ? itemAtual.getQuantidade() : command.quantidade();

        return ItemServico.with(
                itemAtual.getId(),
                ordemServicoId,
                ItemServicoTipo.PECA,
                descricao,
                peca.getId(),
                quantidade,
                peca.getValorUnitario()
        );
    }

    private Peca obterPeca(final AtualizarItemServicoCommand command, final ItemServico itemAtual) {
        if (command.pecaId() != null) {
            return this.pecaGateway.findById(PecaID.from(command.pecaId()))
                    .orElseThrow(() -> DomainException.with(new Error("Peça não encontrada")));
        }

        if (itemAtual.getPecaId() == null) {
            throw DomainException.with(new Error("Peça é obrigatória para item do tipo PECA"));
        }

        return this.pecaGateway.findById(itemAtual.getPecaId())
                .orElseThrow(() -> DomainException.with(new Error("Peça não encontrada")));
    }

    private String obterDescricaoPeca(
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
