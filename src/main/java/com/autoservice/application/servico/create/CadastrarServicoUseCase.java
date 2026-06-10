package com.autoservice.application.servico.create;

import com.autoservice.application.UseCase;
import com.autoservice.domain.servico.Servico;
import com.autoservice.domain.servico.ServicoGateway;

import java.util.Objects;

public class CadastrarServicoUseCase extends UseCase<CadastrarServicoCommand, CadastrarServicoOutput> {

    private final ServicoGateway servicoGateway;

    public CadastrarServicoUseCase(final ServicoGateway servicoGateway) {
        this.servicoGateway = Objects.requireNonNull(servicoGateway);
    }

    @Override
    public CadastrarServicoOutput execute(final CadastrarServicoCommand command) {
        final var servico = Servico.newServico(
                command.nome(),
                command.descricao(),
                command.valorReferencia()
        );
        return CadastrarServicoOutput.from(this.servicoGateway.create(servico));
    }
}
