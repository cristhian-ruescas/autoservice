package com.autoservice.application.ordemservico.aprovacao;

import com.autoservice.application.UseCase;
import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.validation.Error;

import java.util.Objects;

public class ReprovarOrdemServicoUseCase extends UseCase<ReprovarOrdemServicoCommand, OrdemServicoStatusOutput> {

    private final OrdemServicoGateway ordemServicoGateway;

    public ReprovarOrdemServicoUseCase(final OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
    }

    @Override
    public OrdemServicoStatusOutput execute(final ReprovarOrdemServicoCommand command) {
        final var id = OrdemServicoID.from(command.ordemServicoId());

        final var ordemServico = this.ordemServicoGateway.findById(id)
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));

        ordemServico.reprovarOrcamento();

        return OrdemServicoStatusOutput.from(this.ordemServicoGateway.update(ordemServico));
    }
}
