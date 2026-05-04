package com.autoservice.application.ordemservico.itemservico;

import com.autoservice.application.UseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.itemservico.ItemServico;
import com.autoservice.domain.itemservico.ItemServicoGateway;
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

import java.util.List;
import java.util.Objects;

@Service
public class AdicionarItensServicoUseCase extends UseCase<AdicionarItensServicoCommand, AdicionarItensServicoOutput> {

    private final OrdemServicoGateway ordemServicoGateway;
    private final ItemServicoGateway itemServicoGateway;
    private final PecaGateway pecaGateway;

    public AdicionarItensServicoUseCase(
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

        final var ordemServico = this.ordemServicoGateway.findById(ordemServicoId)
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));

        if (ordemServico.getStatus() != OrdemServicoStatus.EM_DIAGNOSTICO) {
            throw DomainException.with(new Error(
                    "Itens de serviço e peças só podem ser adicionados quando a ordem estiver EM_DIAGNOSTICO"
            ));
        }

        final List<AdicionarItemServicoOutput> itens = command.itens().stream()
                .map(item -> criarItem(item, ordemServicoId))
                .map(this.itemServicoGateway::create)
                .map(AdicionarItemServicoOutput::from)
                .toList();

        return AdicionarItensServicoOutput.from(itens);
    }

    private ItemServico criarItem(
            final AdicionarItemServicoCommand command,
            final OrdemServicoID ordemServicoId
    ) {
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

        final Peca peca = this.pecaGateway.findById(PecaID.from(command.pecaId()))
                .orElseThrow(() -> DomainException.with(new Error("Peça não encontrada")));

        return ItemServico.newPeca(
                ordemServicoId,
                command.descricao() == null || command.descricao().isBlank()
                        ? peca.getDescricao()
                        : command.descricao(),
                peca.getId(),
                command.quantidade(),
                peca.getValorUnitario()
        );
    }
}
