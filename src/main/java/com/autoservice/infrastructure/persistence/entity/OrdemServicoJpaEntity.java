package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.ordemservico.valueobject.DataCriacao;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.infrastructure.persistence.converter.DataCriacaoConverter;
import com.autoservice.infrastructure.persistence.converter.OrdemServicoIdConverter;
import com.autoservice.infrastructure.persistence.converter.VeiculoIdConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordem_servico", schema = "servico")
public class OrdemServicoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @Convert(converter = OrdemServicoIdConverter.class)
    private OrdemServicoID id;

    @Column(name = "veiculo_id", nullable = false)
    @Convert(converter = VeiculoIdConverter.class)
    private VeiculoID veiculoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrdemServicoStatus status;

    @Column(name = "data_criacao", nullable = false)
    @Convert(converter = DataCriacaoConverter.class)
    private DataCriacao dataCriacao;

    @Column(name = "relato", nullable = false, length = 1000)
    private String relato;

    @Column(name = "tempo_previsto_execucao_dias")
    private Integer tempoPrevistoExecucaoDias;

    @Column(name = "tempo_previsto_execucao_horas")
    private Integer tempoPrevistoExecucaoHoras;

    @Column(name = "iniciado_em")
    private LocalDateTime iniciadoEm;

    @Column(name = "finalizado_em")
    private LocalDateTime finalizadoEm;

    public OrdemServicoJpaEntity() {
    }

    public OrdemServicoID getId() {
        return id;
    }

    public void setId(final OrdemServicoID id) {
        this.id = id;
    }

    public VeiculoID getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(final VeiculoID veiculoId) {
        this.veiculoId = veiculoId;
    }

    public OrdemServicoStatus getStatus() {
        return status;
    }

    public void setStatus(final OrdemServicoStatus status) {
        this.status = status;
    }

    public DataCriacao getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(final DataCriacao dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getRelato() {
        return relato;
    }

    public void setRelato(final String relato) {
        this.relato = relato;
    }

    public Integer getTempoPrevistoExecucaoDias() {
        return tempoPrevistoExecucaoDias;
    }

    public void setTempoPrevistoExecucaoDias(final Integer tempoPrevistoExecucaoDias) {
        this.tempoPrevistoExecucaoDias = tempoPrevistoExecucaoDias;
    }

    public Integer getTempoPrevistoExecucaoHoras() {
        return tempoPrevistoExecucaoHoras;
    }

    public void setTempoPrevistoExecucaoHoras(final Integer tempoPrevistoExecucaoHoras) {
        this.tempoPrevistoExecucaoHoras = tempoPrevistoExecucaoHoras;
    }

    public LocalDateTime getIniciadoEm() {
        return iniciadoEm;
    }

    public void setIniciadoEm(final LocalDateTime iniciadoEm) {
        this.iniciadoEm = iniciadoEm;
    }

    public LocalDateTime getFinalizadoEm() {
        return finalizadoEm;
    }

    public void setFinalizadoEm(final LocalDateTime finalizadoEm) {
        this.finalizadoEm = finalizadoEm;
    }
}
