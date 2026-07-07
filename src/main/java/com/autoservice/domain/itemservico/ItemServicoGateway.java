package com.autoservice.domain.itemservico;

import com.autoservice.domain.ordemservico.OrdemServicoID;
import com.autoservice.domain.ordemservico.enums.OrdemServicoStatus;
import com.autoservice.domain.peca.PecaID;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ItemServicoGateway {

    ItemServico create(ItemServico itemServico);

    Optional<ItemServico> findById(ItemServicoID id);

    void deleteById(ItemServicoID id);

    BigDecimal totalByOrdemServicoId(OrdemServicoID ordemServicoId);

    List<ItemServico> findByOrdemServicoId(OrdemServicoID ordemServicoId);

    boolean existsByPecaIdAndOrdemServicoStatusNot(PecaID pecaId, OrdemServicoStatus status);
}
