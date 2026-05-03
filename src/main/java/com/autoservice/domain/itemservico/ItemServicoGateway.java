package com.autoservice.domain.itemservico;

import com.autoservice.domain.ordemservico.OrdemServicoID;

import java.math.BigDecimal;
import java.util.List;

public interface ItemServicoGateway {

    ItemServico create(ItemServico itemServico);

    BigDecimal totalByOrdemServicoId(OrdemServicoID ordemServicoId);

    List<ItemServico> findByOrdemServicoId(OrdemServicoID ordemServicoId);
}
