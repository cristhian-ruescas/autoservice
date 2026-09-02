package com.autoservice.infrastructure.persistence.entity;

import com.autoservice.domain.veiculo.valueobject.Ano;
import com.autoservice.domain.veiculo.valueobject.Marca;
import com.autoservice.domain.veiculo.valueobject.Modelo;
import com.autoservice.infrastructure.persistence.converter.AnoConverter;
import com.autoservice.infrastructure.persistence.converter.MarcaConverter;
import com.autoservice.infrastructure.persistence.converter.ModeloConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_veiculo", schema = "cadastro")
public class TipoVeiculoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "marca", nullable = false)
    @Convert(converter = MarcaConverter.class)
    private Marca marca;

    @Column(name = "modelo", nullable = false)
    @Convert(converter = ModeloConverter.class)
    private Modelo modelo;

    @Column(name = "ano", nullable = false)
    @Convert(converter = AnoConverter.class)
    private Ano ano;

    public TipoVeiculoJpaEntity() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Marca getMarca() { return marca; }
    public void setMarca(Marca marca) { this.marca = marca; }
    public Modelo getModelo() { return modelo; }
    public void setModelo(Modelo modelo) { this.modelo = modelo; }
    public Ano getAno() { return ano; }
    public void setAno(Ano ano) { this.ano = ano; }
}
