package com.autoservice.presentation.mapper;

import com.autoservice.domain.workorder.WorkOrder;
import com.autoservice.presentation.dto.WorkOrderDTO;
import com.autoservice.presentation.dto.WorkOrderPartItemDTO;
import com.autoservice.presentation.dto.WorkOrderServiceItemDTO;
import com.autoservice.presentation.dto.WorkOrderStatusDTO;

import java.util.List;

public class WorkOrderMapper {

    private WorkOrderMapper() {
    }

    public static WorkOrderDTO toDTO(final WorkOrder workOrder) {
        final List<WorkOrderServiceItemDTO> services = workOrder.getServices().stream()
                .map(item -> new WorkOrderServiceItemDTO(
                        item.getService().getId().getValue(),
                        item.getService().getName(),
                        item.getPrice()
                ))
                .toList();

        final List<WorkOrderPartItemDTO> parts = workOrder.getParts().stream()
                .map(item -> new WorkOrderPartItemDTO(
                        item.getPart().getId().getValue(),
                        item.getPart().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new WorkOrderDTO(
                workOrder.getId().getValue(),
                workOrder.getCustomer().getId().getValue(),
                workOrder.getVehicle().getId().getValue(),
                workOrder.getStatus(),
                workOrder.getTotalAmount(),
                services,
                parts
        );
    }

    public static WorkOrderStatusDTO toStatusDTO(final WorkOrder workOrder) {
        return new WorkOrderStatusDTO(workOrder.getId().getValue(), workOrder.getStatus());
    }
}

