package com.autoservice.domain.servico;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.validation.Error;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "servico_cadastro", schema = "cadastro")
public class Servico extends AggregateRoot<ServicoID> {

    @EmbeddedId
    private ServicoID id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "valor_referencia", precision = 10, scale = 2)
    private BigDecimal valorReferencia;

    protected Servico() {
        super();
    }

    private Servico(
            final ServicoID id,
            final String nome,
            final String descricao,
            final BigDecimal valorReferencia
    ) {
        super(id);
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.valorReferencia = valorReferencia;
    }

    public static Servico newServico(
            final String nome,
            final String descricao,
            final BigDecimal valorReferencia
    ) {
        final Servico servico = new Servico(ServicoID.unique(), nome, descricao, valorReferencia);
        servico.validateAndThrow();
        return servico;
    }

    public static Servico with(
            final ServicoID id,
            final String nome,
            final String descricao,
            final BigDecimal valorReferencia
    ) {
        final Servico servico = new Servico(id, nome, descricao, valorReferencia);
        servico.validateAndThrow();
        return servico;
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
        if (this.nome == null || this.nome.isBlank()) {
            handler.append(new Error("Nome do serviço não deve ser nulo ou vazio"));
        }

        if (this.nome != null && this.nome.length() > 120) {
            handler.append(new Error("Nome do serviço não deve exceder 120 caracteres"));
        }

        if (this.descricao != null && this.descricao.length() > 500) {
            handler.append(new Error("Descrição do serviço não deve exceder 500 caracteres"));
        }

        if (this.valorReferencia != null && this.valorReferencia.signum() < 0) {
            handler.append(new Error("Valor de referência do serviço não deve ser negativo"));
        }
    }

    @Override
    public ServicoID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValorReferencia() {
        return valorReferencia;
    }
}
