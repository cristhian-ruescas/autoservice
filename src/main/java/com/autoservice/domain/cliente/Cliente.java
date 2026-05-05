package com.autoservice.domain.cliente;

import com.autoservice.domain.AggregateRoot;
import com.autoservice.domain.cliente.validators.ClienteValidator;
import com.autoservice.domain.cliente.valueobject.DataCadastro;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.NotificationValidationHandler;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "cliente", schema = "cadastro")
public class Cliente extends AggregateRoot<ClienteID> {

    @EmbeddedId
    private ClienteID embeddedId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "pessoa_id"))
    })
    private PessoaID pessoaId;

    @Embedded
    @Column(
            name = "data_cadastro",
            nullable = false
    )
    private DataCadastro dataCadastro;

    protected Cliente() {
        super();
    }

    private Cliente(
            ClienteID id,
            PessoaID pessoaId,
            DataCadastro dataCadastro
    ) {
        super(id);
        this.embeddedId = id;
        this.pessoaId = pessoaId;
        this.dataCadastro = dataCadastro;
    }

    public static Cliente newCliente(PessoaID pessoaId) {
        DataCadastro newDataCadastro = DataCadastro.from(LocalDate.now());
        Cliente newCliente = new Cliente(ClienteID.unique(), pessoaId, newDataCadastro);
        final NotificationValidationHandler handler =
                new NotificationValidationHandler();
        newCliente.validate(handler);
        if (handler.hasError()) {
            throw DomainException.with(handler.getErrors());
        }
        return newCliente;
    }

    public static Cliente with(ClienteID id, PessoaID pessoaId, LocalDate dataCadastro) {
        Cliente clienteWith = new Cliente(id, pessoaId, DataCadastro.from(dataCadastro));
        final NotificationValidationHandler handler =
                new NotificationValidationHandler();
        clienteWith.validate(handler);
        return clienteWith;
    }

    @Override
    public void validate(ValidationHandler handler) {
        new ClienteValidator(this, handler).validate();
    }

    @Override
    public ClienteID getId() {
        return embeddedId;
    }

    public PessoaID getPessoaId() {
        return pessoaId;
    }

    public DataCadastro getDataCadastro() {
        return dataCadastro;
    }
}
