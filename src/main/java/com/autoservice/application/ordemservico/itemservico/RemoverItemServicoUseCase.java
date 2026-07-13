package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.application.UnitUseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.itemservico.ItemServicoID;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.validation.Error;

import java.util.Objects;

public class RemoverItemServicoUseCase extends UnitUseCase<RemoverItemServicoCommand> {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ItemServicoGateway itemServicoGateway;

    public RemoverItemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final ItemServicoGateway itemServicoGateway
    ) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.itemServicoGateway = Objects.requireNonNull(itemServicoGateway);
    }

    @Override
    public void execute(final RemoverItemServicoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para remover item de serviço não deve ser nulo"));
        }
        if (command.ordemServicoId() == null) {
            throw DomainException.with(new Error("Ordem de serviço é obrigatória para remover item"));
        }
        if (command.itemServicoId() == null) {
            throw DomainException.with(new Error("Item de serviço é obrigatório para remoção"));
        }

        final var ordemServicoId = OrdemServicoID.from(command.ordemServicoId());
        final var ordemServico = this.ordemServicoGateway.findById(ordemServicoId)
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));

        if (ordemServico.getStatus() != OrdemServicoStatus.EM_DIAGNOSTICO) {
            throw DomainException.with(new Error(
                    "Itens de serviço e peças só podem ser removidos quando a ordem estiver EM_DIAGNOSTICO"
            ));
        }

        final var itemServicoId = ItemServicoID.from(command.itemServicoId());
        final var itemServico = this.itemServicoGateway.findById(itemServicoId)
                .orElseThrow(() -> DomainException.with(new Error("Item de serviço não encontrado")));

        if (!itemServico.getOrdemServicoId().equals(ordemServicoId)) {
            throw DomainException.with(new Error("Item de serviço não pertence à ordem de serviço informada"));
        }

        this.itemServicoGateway.deleteById(itemServicoId);
    }
}
