package com.autoservice.presentation.dto;

import com.autoservice.domain.workorder.WorkOrderStatus;

public record WorkOrderStatusDTO(
        String id,
        WorkOrderStatus status
) {
}

