package com.autoservice.presentation.dto;

import com.autoservice.domain.workorder.WorkOrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateWorkOrderStatusRequest(
        @NotNull WorkOrderStatus status
) {
}

