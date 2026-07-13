package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.infrastructure.persistence.converter.CorConverter;
import com.autoservice.infrastructure.persistence.converter.KilometragemConverter;
import com.autoservice.infrastructure.persistence.converter.PlacaConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "veiculo", schema = "cadastro")
public class VeiculoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "proprietario_id", nullable = false)
    private String proprietarioId;

    @Column(name = "tipo_veiculo_id", nullable = false)
    private String tipoVeiculoId;

    @Column(name = "placa", nullable = false)
    @Convert(converter = PlacaConverter.class)
    private Placa placa;

    @Column(name = "cor", nullable = false)
    @Convert(converter = CorConverter.class)
    private Cor cor;

    @Column(name = "kilometragem", nullable = false)
    @Convert(converter = KilometragemConverter.class)
    private Kilometragem kilometragem;

    public VeiculoJpaEntity() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProprietarioId() { return proprietarioId; }
    public void setProprietarioId(String proprietarioId) { this.proprietarioId = proprietarioId; }
    public String getTipoVeiculoId() { return tipoVeiculoId; }
    public void setTipoVeiculoId(String tipoVeiculoId) { this.tipoVeiculoId = tipoVeiculoId; }
    public Placa getPlaca() { return placa; }
    public void setPlaca(Placa placa) { this.placa = placa; }
    public Cor getCor() { return cor; }
    public void setCor(Cor cor) { this.cor = cor; }
    public Kilometragem getKilometragem() { return kilometragem; }
    public void setKilometragem(Kilometragem kilometragem) { this.kilometragem = kilometragem; }
}
