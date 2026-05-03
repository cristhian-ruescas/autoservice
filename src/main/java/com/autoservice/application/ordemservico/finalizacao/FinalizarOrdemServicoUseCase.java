package com.autoservice.application.ordemservico.finalizacao;

import com.autoservice.application.UseCase;
import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class FinalizarOrdemServicoUseCase extends UseCase<FinalizarOrdemServicoCommand, OrdemServicoStatusOutput> {

    private final OrdemServicoGateway ordemServicoGateway;

    public FinalizarOrdemServicoUseCase(final OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
    }

    @Override
    @Transactional
    public OrdemServicoStatusOutput execute(final FinalizarOrdemServicoCommand command) {
        final var id = OrdemServicoID.from(command.ordemServicoId());

        final var ordemServico = this.ordemServicoGateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));

        ordemServico.finalizarExecucao();

        return OrdemServicoStatusOutput.from(this.ordemServicoGateway.update(ordemServico));
    }
}
