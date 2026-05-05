package com.autoservice.application.servico.update;

import com.autoservice.application.UseCase;
import com.autoservice.application.servico.query.ServicoOutput;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.servico.Servico;
import com.autoservice.domain.servico.ServicoGateway;
import com.autoservice.domain.servico.ServicoID;
import com.autoservice.validation.Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AtualizarServicoUseCase extends UseCase<AtualizarServicoCommand, ServicoOutput> {

    private final ServicoGateway servicoGateway;

    public AtualizarServicoUseCase(final ServicoGateway servicoGateway) {
        this.servicoGateway = Objects.requireNonNull(servicoGateway);
    }

    @Override
    @Transactional
    public ServicoOutput execute(final AtualizarServicoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para atualizar serviço não deve ser nulo"));
        }
        if (command.servicoId() == null) {
            throw DomainException.with(new Error("Serviço é obrigatório para atualização"));
        }

        final var servicoId = ServicoID.from(command.servicoId());
        final var atual = this.servicoGateway.findById(servicoId)
                .orElseThrow(() -> DomainException.with(new Error("Serviço não encontrado")));

        final var atualizado = Servico.with(
                servicoId,
                command.nome() == null ? atual.getNome() : command.nome(),
                command.descricao() == null ? atual.getDescricao() : command.descricao(),
                command.valorReferencia() == null ? atual.getValorReferencia() : command.valorReferencia()
        );

        return ServicoOutput.from(this.servicoGateway.update(atualizado));
    }
}
