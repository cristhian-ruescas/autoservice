package com.autoservice.presentation.dto;

import com.autoservice.domain.workorder.WorkOrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record WorkOrderDTO(
        String id,
        String clienteId,
        String veiculoId,
        WorkOrderStatus status,
        BigDecimal valorTotal,
        List<WorkOrderServiceItemDTO> servicos,
        List<WorkOrderPartItemDTO> pecas
) {
}

