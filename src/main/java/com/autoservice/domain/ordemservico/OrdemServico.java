package com.autoservice.domain.ordemservico;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.events.*;
import com.autoservice.domain.ordemservico.validators.OrdemServicoValidator;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

        final NotificationValidationHandler handler = new NotificationValidationHandler();
        ordemServico.validate(handler);

        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }

        ordemServico.registerEvent(new OrdemServicoCriadaEvent(ordemServico.getId(), ordemServico.getVeiculoId()));

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

        final NotificationValidationHandler handler = new NotificationValidationHandler();
        ordemServico.validate(handler);

        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }

        return ordemServico;
    }

    public void iniciarDiagnostico() {
        if (this.status != OrdemServicoStatus.RECEBIDO) {
            throw DomainException.with(List.of(
                    new Error("Ordem de serviço precisa estar RECEBIDO para iniciar diagnóstico")
            ));
        }

        this.status = OrdemServicoStatus.EM_DIAGNOSTICO;
        this.registerEvent(new OrdemServicoDiagnosticoIniciadoEvent(this.getId()));
    }

    public void finalizarDiagnostico(
            final Integer tempoPrevistoExecucaoDias,
            final Integer tempoPrevistoExecucaoHoras
    ) {
        if (this.status != OrdemServicoStatus.EM_DIAGNOSTICO) {
            throw DomainException.with(List.of(
                    new Error("Ordem de serviço precisa estar EM_DIAGNOSTICO para finalizar diagnóstico")
            ));
        }
        final int dias = tempoPrevistoExecucaoDias == null ? 0 : tempoPrevistoExecucaoDias;
        final int horas = tempoPrevistoExecucaoHoras == null ? 0 : tempoPrevistoExecucaoHoras;

        if (dias < 0) {
            throw DomainException.with(List.of(
                    new Error("Tempo previsto de execução em dias precisa ser maior ou igual a zero")
            ));
        }
        if (horas < 0 || horas > 23) {
            throw DomainException.with(List.of(
                    new Error("Tempo previsto de execução em horas precisa estar entre 0 e 23")
            ));
        }
        if (dias == 0 && horas == 0) {
            throw DomainException.with(List.of(
                    new Error("Tempo previsto de execução precisa ser maior que zero")
            ));
        }

        this.tempoPrevistoExecucaoDias = dias;
        this.tempoPrevistoExecucaoHoras = horas;
        this.status = OrdemServicoStatus.AGUARDANDO_APROVACAO;
        this.registerEvent(new OrdemServicoDiagnosticoFinalizadoEvent(this.getId()));
    }

    public void finalizarDiagnostico() {
        this.finalizarDiagnostico(0, 1);
    }

    public void aprovarOrcamento() {
        if (this.status != OrdemServicoStatus.AGUARDANDO_APROVACAO) {
            throw DomainException.with(List.of(
                    new Error("Ordem de serviço precisa estar AGUARDANDO_APROVACAO para aprovar orçamento")
            ));
        }

        this.status = OrdemServicoStatus.EM_EXECUCAO;
        this.iniciadoEm = LocalDateTime.now();
        this.registerEvent(new OrdemServicoOrcamentoAprovadoEvent(this.getId()));
    }

    public void reprovarOrcamento() {
        if (this.status != OrdemServicoStatus.AGUARDANDO_APROVACAO) {
            throw DomainException.with(List.of(
                    new Error("Ordem de serviço precisa estar AGUARDANDO_APROVACAO para reprovar orçamento")
            ));
        }

        this.status = OrdemServicoStatus.REPROVADO;
    }

    public void finalizarExecucao() {
        if (this.status != OrdemServicoStatus.EM_EXECUCAO) {
            throw DomainException.with(List.of(
                    new Error("Ordem de serviço precisa estar EM_EXECUCAO para ser finalizada")
            ));
        }

        this.status = OrdemServicoStatus.FINALIZADA;
        this.finalizadoEm = LocalDateTime.now();
        this.registerEvent(new OrdemServicoFinalizadaEvent(this.getId()));
    }

    public void entregar() {
        if (this.status != OrdemServicoStatus.FINALIZADA && this.status != OrdemServicoStatus.REPROVADO) {
            throw DomainException.with(List.of(
                    new Error("Ordem de serviço precisa estar FINALIZADA ou REPROVADO para ser entregue")
            ));
        }

        this.status = OrdemServicoStatus.ENTREGUE;
    }

    public void cancelar() {
        if (this.status == OrdemServicoStatus.FINALIZADA
                || this.status == OrdemServicoStatus.ENTREGUE
                || this.status == OrdemServicoStatus.REPROVADO) {
            throw DomainException.with(List.of(
                    new Error("Ordem de serviço FINALIZADA, ENTREGUE ou REPROVADO não pode ser cancelada")
            ));
        }

        this.status = OrdemServicoStatus.CANCELADO;
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
