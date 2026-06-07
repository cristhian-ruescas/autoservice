package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.application.UseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.validation.Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class AdicionarItensServicoUseCase extends UseCase<AdicionarItensServicoCommand, AdicionarItensServicoOutput> {

    private final ItemServicoOrchestrator itemServicoOrchestrator;
    private final ItemServicoGateway itemServicoGateway;

    public AdicionarItensServicoUseCase(
            final ItemServicoOrchestrator itemServicoOrchestrator,
            final ItemServicoGateway itemServicoGateway
    ) {
        this.itemServicoOrchestrator = Objects.requireNonNull(itemServicoOrchestrator);
        this.itemServicoGateway = Objects.requireNonNull(itemServicoGateway);
    }

    @Override
    @Transactional
    public AdicionarItensServicoOutput execute(final AdicionarItensServicoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para adicionar itens de serviço não deve ser nulo"));
        }
        if (command.itens() == null || command.itens().isEmpty()) {
            throw DomainException.with(new Error("Informe ao menos um item de serviço"));
        }
        if (command.ordemServicoId() == null) {
            throw DomainException.with(new Error("Ordem de serviço é obrigatória para adicionar itens"));
        }

        final var ordemServicoId = OrdemServicoID.from(command.ordemServicoId());
        this.itemServicoOrchestrator.exigirOrdemEmDiagnostico(ordemServicoId);

        final List<AdicionarItemServicoOutput> itens = command.itens().stream()
                .map(item -> this.itemServicoOrchestrator.criar(item, ordemServicoId))
                .map(this.itemServicoGateway::create)
                .map(AdicionarItemServicoOutput::from)
                .toList();

        return AdicionarItensServicoOutput.from(itens);
    }
}
