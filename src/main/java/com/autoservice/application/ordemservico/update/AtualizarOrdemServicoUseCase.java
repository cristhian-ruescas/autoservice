package com.autoservice.application.ordemservico.update;

import com.autoservice.application.UseCase;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.OrdemServico;
import com.autoservice.domain.ordemservico.OrdemServicoGateway;
import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.veiculo.VeiculoGateway;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.validation.Error;
import com.autoservice.validation.handler.NotificationValidationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AtualizarOrdemServicoUseCase extends UseCase<AtualizarOrdemServicoCommand, AtualizarOrdemServicoOutput> {

    private final OrdemServicoGateway ordemServicoGateway;
    private final VeiculoGateway veiculoGateway;

    public AtualizarOrdemServicoUseCase(
            final OrdemServicoGateway ordemServicoGateway,
            final VeiculoGateway veiculoGateway
    ) {
        this.ordemServicoGateway = Objects.requireNonNull(ordemServicoGateway);
        this.veiculoGateway = Objects.requireNonNull(veiculoGateway);
    }

    @Override
    @Transactional
    public AtualizarOrdemServicoOutput execute(final AtualizarOrdemServicoCommand command) {
        if (command == null) {
            throw DomainException.with(new Error("Comando para atualizar ordem de serviço não deve ser nulo"));
        }
        if (command.ordemServicoId() == null) {
            throw DomainException.with(new Error("Ordem de serviço é obrigatória para atualização"));
        }

        final var ordemServico = this.ordemServicoGateway.findById(OrdemServicoID.from(command.ordemServicoId()))
                .orElseThrow(() -> DomainException.with(new Error("Ordem de serviço não encontrada")));
        final var veiculoId = obterVeiculoId(command, ordemServico);
        final var relato = command.relato() == null ? ordemServico.getRelato() : command.relato();
        final var tempoPrevistoExecucaoDias = command.tempoPrevistoExecucaoDias() == null
                ? ordemServico.getTempoPrevistoExecucaoDias()
                : command.tempoPrevistoExecucaoDias();
        final var tempoPrevistoExecucaoHoras = command.tempoPrevistoExecucaoHoras() == null
                ? ordemServico.getTempoPrevistoExecucaoHoras()
                : command.tempoPrevistoExecucaoHoras();

        validarTempoPrevisto(tempoPrevistoExecucaoDias, tempoPrevistoExecucaoHoras);

        final var ordemServicoAtualizada = OrdemServico.with(
                ordemServico.getId(),
                veiculoId,
                ordemServico.getStatus(),
                ordemServico.getDataCriacao(),
                relato,
                tempoPrevistoExecucaoDias,
                tempoPrevistoExecucaoHoras,
                ordemServico.getIniciadoEm(),
                ordemServico.getFinalizadoEm()
        );
        validate(ordemServicoAtualizada);

        return AtualizarOrdemServicoOutput.from(this.ordemServicoGateway.update(ordemServicoAtualizada));
    }

    private VeiculoID obterVeiculoId(
            final AtualizarOrdemServicoCommand command,
            final OrdemServico ordemServico
    ) {
        if (command.veiculoId() == null) {
            return ordemServico.getVeiculoId();
        }

        final var veiculoId = VeiculoID.from(command.veiculoId());
        this.veiculoGateway.findById(veiculoId)
                .orElseThrow(() -> DomainException.with(new Error("Veículo não encontrado")));

        return veiculoId;
    }

    private void validarTempoPrevisto(final Integer dias, final Integer horas) {
        if (dias != null && dias < 0) {
            throw DomainException.with(new Error("Tempo previsto de execução em dias precisa ser maior ou igual a zero"));
        }
        if (horas != null && (horas < 0 || horas > 23)) {
            throw DomainException.with(new Error("Tempo previsto de execução em horas precisa estar entre 0 e 23"));
        }
    }

    private void validate(final OrdemServico ordemServico) {
        final var handler = new NotificationValidationHandler();
        ordemServico.validate(handler);

        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
    }
}
