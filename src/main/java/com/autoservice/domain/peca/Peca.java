package com.autoservice.domain.peca;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.estoque.EstoqueID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.tipoveiculo.TipoVeiculoID;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "peca", schema = "estoque")
public class Peca extends AggregateRoot<PecaID> {

    @EmbeddedId
    private PecaID id;

    @Column(name = "descricao", nullable = false, length = 180)
    private String descricao;

    @Column(name = "codigo", nullable = false, unique = true, length = 80)
    private String codigo;

    @Column(name = "marca", nullable = false, length = 120)
    private String marca;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "estoque_id"))
    private EstoqueID estoqueId;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "tipo_veiculo_id"))
    private TipoVeiculoID tipoVeiculoId;

    protected Peca() {
        super();
    }

    private Peca(
            final PecaID id,
            final String descricao,
            final String codigo,
            final String marca,
            final BigDecimal valorUnitario,
            final EstoqueID estoqueId,
            final TipoVeiculoID tipoVeiculoId
    ) {
        super(id);
        this.id = id;
        this.descricao = descricao;
        this.codigo = codigo;
        this.marca = marca;
        this.valorUnitario = valorUnitario;
        this.estoqueId = estoqueId;
        this.tipoVeiculoId = tipoVeiculoId;
    }

    public static Peca newPeca(
            final String descricao,
            final String codigo,
            final String marca,
            final BigDecimal valorUnitario,
            final EstoqueID estoqueId,
            final TipoVeiculoID tipoVeiculoId
    ) {
        final Peca peca = new Peca(
                PecaID.unique(),
                descricao,
                codigo,
                marca,
                valorUnitario,
                estoqueId,
                tipoVeiculoId
        );
        peca.validateAndThrow();
        return peca;
    }

    public static Peca with(
            final PecaID id,
            final String descricao,
            final String codigo,
            final String marca,
            final BigDecimal valorUnitario,
            final EstoqueID estoqueId,
            final TipoVeiculoID tipoVeiculoId
    ) {
        final Peca peca = new Peca(id, descricao, codigo, marca, valorUnitario, estoqueId, tipoVeiculoId);
        peca.validateAndThrow();
        return peca;
    }

    private void validateAndThrow() {
        final NotificationValidationHandler handler = new NotificationValidationHandler();
        this.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
    }

    @Override
    public void validate(final ValidationHandler handler) {
        if (this.descricao == null || this.descricao.isBlank()) {
            handler.append(new Error("Descrição da peça não deve ser nula ou vazia"));
        }

        if (this.codigo == null || this.codigo.isBlank()) {
            handler.append(new Error("Código da peça não deve ser nulo ou vazio"));
        }

        if (this.marca == null || this.marca.isBlank()) {
            handler.append(new Error("Marca da peça não deve ser nula ou vazia"));
        }

        if (this.marca != null && this.marca.length() > 120) {
            handler.append(new Error("Marca da peça não deve exceder 120 caracteres"));
        }

        if (this.valorUnitario == null || this.valorUnitario.signum() < 0) {
            handler.append(new Error("Valor unitário da peça não deve ser nulo ou negativo"));
        }

    }

    @Override
    public PecaID getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMarca() {
        return marca;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public EstoqueID getEstoqueId() {
        return estoqueId;
    }

    public TipoVeiculoID getTipoVeiculoId() {
        return tipoVeiculoId;
    }

    public void vincularEstoque(final EstoqueID estoqueId) {
        if (estoqueId == null) {
            throw DomainException.with(java.util.List.of(
                    new Error("Estoque da peça não deve ser nulo")
            ));
        }

        this.estoqueId = estoqueId;
    }
}
