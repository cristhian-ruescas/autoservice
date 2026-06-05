package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.domain.veiculo.VeiculoID;
import com.autoservice.domain.veiculo.valueobject.Cor;
import com.autoservice.domain.veiculo.valueobject.Kilometragem;
import com.autoservice.domain.veiculo.valueobject.Placa;
import com.autoservice.infrastructure.persistence.converter.CorConverter;
import com.autoservice.infrastructure.persistence.converter.KilometragemConverter;
import com.autoservice.infrastructure.persistence.converter.PlacaConverter;
import com.autoservice.infrastructure.persistence.converter.PessoaIdConverter;
import com.autoservice.infrastructure.persistence.converter.TipoVeiculoIdConverter;
import com.autoservice.infrastructure.persistence.converter.VeiculoIdConverter;
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
    @Convert(converter = VeiculoIdConverter.class)
    private VeiculoID id;

    @Column(name = "proprietario_id", nullable = false)
    @Convert(converter = PessoaIdConverter.class)
    private PessoaID proprietarioId;

    @Column(name = "tipo_veiculo_id", nullable = false)
    @Convert(converter = TipoVeiculoIdConverter.class)
    private TipoVeiculoID tipoVeiculoId;

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

    public VeiculoID getId() { return id; }
    public void setId(VeiculoID id) { this.id = id; }
    public PessoaID getProprietarioId() { return proprietarioId; }
    public void setProprietarioId(PessoaID proprietarioId) { this.proprietarioId = proprietarioId; }
    public TipoVeiculoID getTipoVeiculoId() { return tipoVeiculoId; }
    public void setTipoVeiculoId(TipoVeiculoID tipoVeiculoId) { this.tipoVeiculoId = tipoVeiculoId; }
    public Placa getPlaca() { return placa; }
    public void setPlaca(Placa placa) { this.placa = placa; }
    public Cor getCor() { return cor; }
    public void setCor(Cor cor) { this.cor = cor; }
    public Kilometragem getKilometragem() { return kilometragem; }
    public void setKilometragem(Kilometragem kilometragem) { this.kilometragem = kilometragem; }
}
