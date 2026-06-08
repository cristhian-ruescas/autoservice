package com.autoservice.application.cliente.delete;

import com.autoservice.domain.cliente.Cliente;
import com.autoservice.domain.cliente.ClienteGateway;
import com.autoservice.domain.cliente.ClienteID;
import com.autoservice.domain.exceptions.DomainException;
import com.autoservice.domain.pessoa.PessoaID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverClienteUseCaseTest {

    @Mock
    private ClienteGateway clienteGateway;

    @InjectMocks
    private RemoverClienteUseCase useCase;

    @Test
    void removeClienteExistente() {
        final var cid = ClienteID.unique();
        final var cliente = Cliente.with(cid, PessoaID.unique(), LocalDate.now());

        when(clienteGateway.findById(cid)).thenReturn(Optional.of(cliente));

        useCase.execute(RemoverClienteCommand.with(UUID.fromString(cid.getValue())));

        verify(clienteGateway).deleteById(cid);
    }

    @Test
    void clienteNaoEncontrado() {
        final var cid = ClienteID.unique();
        when(clienteGateway.findById(cid)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> useCase.execute(RemoverClienteCommand.with(UUID.fromString(cid.getValue()))));
    }
}
