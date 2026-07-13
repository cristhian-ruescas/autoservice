package com.autoservice.application.veiculo.query;

import java.util.List;
import java.util.UUID;

public interface ListVeiculosByClienteQuery {

    List<VeiculoOutput> listarPorCliente(UUID clienteId);
}
