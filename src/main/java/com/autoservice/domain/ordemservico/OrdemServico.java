package com.autoservice.domain.ordemservico;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.events.*;
import com.autoservice.domain.ordemservico.validators.OrdemServicoValidator;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrdemServico extends AggregateRoot<OrdemServicoID> {

    private OrdemServicoID id;
    private VeiculoID veiculoId;
    private OrdemServicoStatus status;
    private DataCriacao dataCriacao;
    private String relato;
    private Integer tempoPrevistoExecucaoDias;
    private Integer tempoPrevistoExecucaoHoras;
    private LocalDateTime iniciadoEm;
    private LocalDateTime finalizadoEm;

    protected OrdemServico() {
        super();
    }

    private OrdemServico(
            final OrdemServicoID id,
            final VeiculoID veiculoId,
            final OrdemServicoStatus status,
            final DataCriacao dataCriacao,
            final String relato,
            final Integer tempoPrevistoExecucaoDias,
            final Integer tempoPrevistoExecucaoHoras,
            final LocalDateTime iniciadoEm,
            final LocalDateTime finalizadoEm
    ) {
        super(id);
        this.id = id;
        this.veiculoId = veiculoId;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.relato = relato;
        this.tempoPrevistoExecucaoDias = tempoPrevistoExecucaoDias;
        this.tempoPrevistoExecucaoHoras = tempoPrevistoExecucaoHoras;
        this.iniciadoEm = iniciadoEm;
        this.finalizadoEm = finalizadoEm;
    }

    public static OrdemServico newOrdemServico(final VeiculoID veiculoId, final String relato) {
        final OrdemServico ordemServico = new OrdemServico(
                OrdemServicoID.unique(),
                veiculoId,
                OrdemServicoStatus.RECEBIDO,
                DataCriacao.from(LocalDate.now()),
                relato,
                null,
                null,
                null,
                null
        );

        ordemServico.validateAndThrow();
        ordemServico.registerEvent(new OrdemServicoCriadaEvent(ordemServico.getId(), ordemServico.getVeiculoId()));
        ordemServico.registrarAlteracaoStatus(null);

        return ordemServico;
    }

    public static OrdemServico with(
            final OrdemServicoID id,
            final VeiculoID veiculoId,
            final OrdemServicoStatus status,
            final DataCriacao dataCriacao,
            final String relato
    ) {
        return with(id, veiculoId, status, dataCriacao, relato, null, null, null, null);
    }

    public static OrdemServico with(
            final OrdemServicoID id,
            final VeiculoID veiculoId,
            final OrdemServicoStatus status,
            final DataCriacao dataCriacao,
            final String relato,
            final Integer tempoPrevistoExecucaoDias,
            final Integer tempoPrevistoExecucaoHoras,
            final LocalDateTime iniciadoEm,
            final LocalDateTime finalizadoEm
    ) {
        final OrdemServico ordemServico = new OrdemServico(
                id,
                veiculoId,
                status,
                dataCriacao,
                relato,
                tempoPrevistoExecucaoDias,
                tempoPrevistoExecucaoHoras,
                iniciadoEm,
                finalizadoEm
        );

        ordemServico.validateAndThrow();
        return ordemServico;
    }

    public void iniciarDiagnostico() {
        final var statusAnterior = this.status;
        this.status = OrdemServicoStateMachine.iniciarDiagnostico(this.status);
        this.registerEvent(new OrdemServicoDiagnosticoIniciadoEvent(this.getId()));
        registrarAlteracaoStatus(statusAnterior);
    }

    public void finalizarDiagnostico(
            final Integer tempoPrevistoExecucaoDias,
            final Integer tempoPrevistoExecucaoHoras
    ) {
        final var statusAnterior = this.status;
        final var transicao = OrdemServicoStateMachine.finalizarDiagnostico(
                this.status,
                tempoPrevistoExecucaoDias,
                tempoPrevistoExecucaoHoras
        );

        this.tempoPrevistoExecucaoDias = transicao.tempoPrevistoExecucaoDias();
        this.tempoPrevistoExecucaoHoras = transicao.tempoPrevistoExecucaoHoras();
        this.status = transicao.status();
        this.registerEvent(new OrdemServicoDiagnosticoFinalizadoEvent(this.getId()));
        registrarAlteracaoStatus(statusAnterior);
    }

    public void finalizarDiagnostico() {
        finalizarDiagnostico(0, 1);
    }

    public void aprovarOrcamento() {
        final var statusAnterior = this.status;
        final var transicao = OrdemServicoStateMachine.aprovarOrcamento(this.status);

        this.status = transicao.status();
        this.iniciadoEm = transicao.iniciadoEm();
        this.registerEvent(new OrdemServicoOrcamentoAprovadoEvent(this.getId()));
        registrarAlteracaoStatus(statusAnterior);
    }

    public void reprovarOrcamento() {
        final var statusAnterior = this.status;
        this.status = OrdemServicoStateMachine.reprovarOrcamento(this.status);
        registrarAlteracaoStatus(statusAnterior);
    }

    public void finalizarExecucao() {
        final var statusAnterior = this.status;
        this.status = OrdemServicoStateMachine.finalizarExecucao(this.status);
        this.finalizadoEm = LocalDateTime.now();
        this.registerEvent(new OrdemServicoFinalizadaEvent(this.getId()));
        registrarAlteracaoStatus(statusAnterior);
    }

    public void entregar() {
        final var statusAnterior = this.status;
        this.status = OrdemServicoStateMachine.entregar(this.status);
        registrarAlteracaoStatus(statusAnterior);
    }

    public void cancelar() {
        final var statusAnterior = this.status;
        this.status = OrdemServicoStateMachine.cancelar(this.status);
        registrarAlteracaoStatus(statusAnterior);
    }

    private void registrarAlteracaoStatus(final OrdemServicoStatus statusAnterior) {
        this.registerEvent(new OrdemServicoStatusAlteradoEvent(this.getId(), statusAnterior, this.status));
    }

    private void validateAndThrow() {
        final NotificationValidationHandler handler = new NotificationValidationHandler();
        validate(handler);

        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
    }

    @Override
    public void validate(final ValidationHandler handler) {
        new OrdemServicoValidator(this, handler).validate();
    }

    @Override
    public OrdemServicoID getId() {
        return id;
    }

    public VeiculoID getVeiculoId() {
        return veiculoId;
    }

    public OrdemServicoStatus getStatus() {
        return status;
    }

    public DataCriacao getDataCriacao() {
        return dataCriacao;
    }

    public String getRelato() {
        return relato;
    }

    public Integer getTempoPrevistoExecucaoDias() {
        return tempoPrevistoExecucaoDias;
    }

    public Integer getTempoPrevistoExecucaoHoras() {
        return tempoPrevistoExecucaoHoras;
    }

    public LocalDateTime getIniciadoEm() {
        return iniciadoEm;
    }

    public LocalDateTime getFinalizadoEm() {
        return finalizadoEm;
    }
}
