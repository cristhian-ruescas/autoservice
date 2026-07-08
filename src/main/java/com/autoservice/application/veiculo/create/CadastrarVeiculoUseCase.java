package com.autoservice.application.veiculo.create;

import com.autoservice.application.UseCase;
import com.autoservice.application.tipoveiculo.TipoVeiculoResolver;
import com.autoservice.application.veiculo.query.VeiculoOutput;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.veiculo.Veiculo;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.validation.Error;

import java.util.Objects;

public class CadastrarVeiculoUseCase extends UseCase<CadastrarVeiculoCommand, VeiculoOutput> {

    private final VeiculoGateway veiculoGateway;
    private final ClienteGateway clienteGateway;
    private final TipoVeiculoResolver tipoVeiculoResolver;

    public CadastrarVeiculoUseCase(
            final VeiculoGateway veiculoGateway,
            final ClienteGateway clienteGateway,
            final TipoVeiculoResolver tipoVeiculoResolver
    ) {
        this.veiculoGateway = Objects.requireNonNull(veiculoGateway);
        this.clienteGateway = Objects.requireNonNull(clienteGateway);
        this.tipoVeiculoResolver = Objects.requireNonNull(tipoVeiculoResolver);
    }

    @Override
    public VeiculoOutput execute(final CadastrarVeiculoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para cadastrar veículo não deve ser nulo"));
        }
        if (command.clienteId() == null) {
            throw DomainException.with(new Error("Cliente proprietário do veículo é obrigatório"));
        }

        final var cliente = this.clienteGateway.findById(ClienteID.from(command.clienteId()))
                .orElseThrow(() -> DomainException.with(new Error("Cliente proprietário não encontrado")));

        final var tipoVeiculo = this.tipoVeiculoResolver.obterOuCriar(
                command.marca(),
                command.modelo(),
                command.ano()
        );

        final var veiculo = this.veiculoGateway.create(Veiculo.newVeiculo(
                cliente.getPessoaId(),
                tipoVeiculo.getId(),
                Placa.from(command.placa()),
                Cor.from(command.cor()),
                Kilometragem.from(command.kilometragem())
        ));

        return new VeiculoOutput(
                veiculo.getId().getValue(),
                veiculo.getPlaca().getValue(),
                tipoVeiculo.getMarca().getValue(),
                tipoVeiculo.getModelo().getValue(),
                tipoVeiculo.getAno().getValue(),
                veiculo.getCor().getValue(),
                veiculo.getKilometragem().getValue(),
                null
        );
    }
}
