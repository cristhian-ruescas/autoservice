package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.application.UseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.itemservico.ItemServicoID;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class AtualizarItemServicoUseCase extends UseCase<AtualizarItemServicoCommand, AdicionarItemServicoOutput> {

    private final ItemServicoOrchestrator itemServicoOrchestrator;
    private final ItemServicoGateway itemServicoGateway;

    public AtualizarItemServicoUseCase(
            final ItemServicoOrchestrator itemServicoOrchestrator,
            final ItemServicoGateway itemServicoGateway
    ) {
        this.itemServicoOrchestrator = Objects.requireNonNull(itemServicoOrchestrator);
        this.itemServicoGateway = Objects.requireNonNull(itemServicoGateway);
    }

    @Override
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
        this.itemServicoOrchestrator.exigirOrdemEmDiagnostico(ordemServicoId);

        final var itemAtual = this.itemServicoGateway.findById(ItemServicoID.from(command.itemServicoId()))
                .orElseThrow(() -> DomainException.with(new Error("Item de serviço não encontrado")));

        if (!itemAtual.getOrdemServicoId().equals(ordemServicoId)) {
            throw DomainException.with(new Error("Item de serviço não pertence à ordem de serviço informada"));
        }

        final var itemAtualizado = this.itemServicoOrchestrator.mesclar(command, itemAtual, ordemServicoId);

        return AdicionarItemServicoOutput.from(this.itemServicoGateway.update(itemAtualizado));
    }
}
