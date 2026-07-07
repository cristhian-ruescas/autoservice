package com.autoservice.application.ordemservico.delete;

import com.autoservice.application.UseCase;
import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class RemoverOrdemServicoUseCase extends UseCase<RemoverOrdemServicoCommand, OrdemServicoStatusOutput> {

    private final OrdemServicoGateway ordemServicoGateway;

    public RemoverOrdemServicoUseCase(final OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
    }

    @Override
    public OrdemServicoStatusOutput execute(final RemoverOrdemServicoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para remover ordem de serviço não deve ser nulo"));
        }
        if (command.ordemServicoId() == null) {
            throw DomainException.with(new Error("Ordem de serviço é obrigatória para remoção"));
        }

        final var ordemServico = this.ordemServicoGateway.findById(OrdemServicoID.from(command.ordemServicoId()))
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));

        ordemServico.cancelar();

        return OrdemServicoStatusOutput.from(this.ordemServicoGateway.create(ordemServico));
    }
}
