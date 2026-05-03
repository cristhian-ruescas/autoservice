package com.autoservice.application.peca.create;

import com.autoservice.application.UseCase;
import com.autoservice.domain.peca.Peca;
import com.autoservice.domain.peca.PecaGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoGateway;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CadastrarPecaUseCase extends UseCase<CadastrarPecaCommand, CadastrarPecaOutput> {

    private final PecaGateway pecaGateway;
    private final TipoVeiculoGateway tipoVeiculoGateway;

    public CadastrarPecaUseCase(
            final PecaGateway pecaGateway,
            final TipoVeiculoGateway tipoVeiculoGateway
    ) {
        this.pecaGateway = Objects.requireNonNull(pecaGateway);
        this.tipoVeiculoGateway = Objects.requireNonNull(tipoVeiculoGateway);
    }

    @Override
    @Transactional
    public CadastrarPecaOutput execute(final CadastrarPecaCommand command) {
        final var peca = Peca.newPeca(
                command.descricao(),
                command.codigo(),
                command.marca(),
                command.valorUnitario(),
                null,
                tipoVeiculoIdOf(command)
        );
        final var pecaCriada = this.pecaGateway.create(peca);

        return CadastrarPecaOutput.from(pecaCriada);
    }

    private TipoVeiculoID tipoVeiculoIdOf(final CadastrarPecaCommand command) {
        if (command.tipoVeiculoId() == null) {
            return null;
        }

        final var tipoVeiculoId = TipoVeiculoID.from(command.tipoVeiculoId());
        this.tipoVeiculoGateway.findById(tipoVeiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de veículo não encontrado"));

        return tipoVeiculoId;
    }
}
