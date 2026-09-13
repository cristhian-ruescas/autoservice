package com.autoservice.application.cliente.query;

public interface GetClienteByCpfQuery {

    ClienteOutput buscarPorCpf(String cpf);
}
