package com.autoservice.domain.cliente;

import com.autoservice.domain.pessoa.PessoaID;

import java.util.Optional;

public interface ClienteGateway {

    Cliente create(Cliente cliente);

    Optional<Cliente> findById(ClienteID id);

    Optional<Cliente> findByPessoaId(PessoaID pessoaId);

    void deleteById(ClienteID id);
}
