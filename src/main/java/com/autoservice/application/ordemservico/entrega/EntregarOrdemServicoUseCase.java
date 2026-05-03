package com.autoservice.application.ordemservico.entrega;

import com.autoservice.application.UseCase;
import com.autoservice.application.ordemservico.status.OrdemServicoStatusOutput;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class EntregarOrdemServicoUseCase extends UseCase<EntregarOrdemServicoCommand, OrdemServicoStatusOutput> {

    private final OrdemServicoGateway ordemServicoGateway;

    public EntregarOrdemServicoUseCase(final OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
    }

    @Override
    @Transactional
    public OrdemServicoStatusOutput execute(final EntregarOrdemServicoCommand command) {
        final var id = OrdemServicoID.from(command.ordemServicoId());

        final var ordemServico = this.ordemServicoGateway.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ordem de serviço não encontrada"));

        ordemServico.entregar();

        return OrdemServicoStatusOutput.from(this.ordemServicoGateway.update(ordemServico));
    }
}
