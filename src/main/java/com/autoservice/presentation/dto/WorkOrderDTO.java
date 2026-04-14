package com.autoservice.presentation.dto;

import com.autoservice.domain.workorder.WorkOrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record WorkOrderDTO(
        String id,
        String customerId,
        String vehicleId,
        WorkOrderStatus status,
        BigDecimal totalAmount,
        List<WorkOrderServiceItemDTO> services,
        List<WorkOrderPartItemDTO> parts
) {
}

