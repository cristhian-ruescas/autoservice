package com.autoservice.presentation.controller;

import com.autoservice.application.WorkOrderService;
import com.autoservice.domain.workorder.WorkOrder;
import com.autoservice.domain.workorder.WorkOrderID;
import com.autoservice.presentation.dto.*;
import com.autoservice.presentation.mapper.WorkOrderMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/work-orders")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(final WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @PostMapping
    public ResponseEntity<WorkOrderDTO> create(@Valid @RequestBody final CreateWorkOrderRequest request) {
        final WorkOrder workOrder = workOrderService.create(
                request.customerId(),
                request.vehicleId(),
                request.serviceIds(),
                mapPartInputs(request.parts())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkOrderMapper.toDTO(workOrder));
    }

    @GetMapping
    public ResponseEntity<List<WorkOrderDTO>> findAll() {
        final List<WorkOrderDTO> workOrders = workOrderService.findAll().stream().map(WorkOrderMapper::toDTO).toList();
        return ResponseEntity.ok(workOrders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderDTO> findById(@PathVariable final String id) {
        return workOrderService.findById(WorkOrderID.from(id))
                .map(WorkOrderMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkOrderDTO> update(@PathVariable final String id, @Valid @RequestBody final UpdateWorkOrderRequest request) {
        final WorkOrder workOrder = workOrderService.update(
                WorkOrderID.from(id),
                request.customerId(),
                request.vehicleId(),
                request.serviceIds(),
                mapPartInputs(request.parts())
        );
        return ResponseEntity.ok(WorkOrderMapper.toDTO(workOrder));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrderDTO> updateStatus(@PathVariable final String id, @Valid @RequestBody final UpdateWorkOrderStatusRequest request) {
        final WorkOrder workOrder = workOrderService.updateStatus(WorkOrderID.from(id), request.status());
        return ResponseEntity.ok(WorkOrderMapper.toDTO(workOrder));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<WorkOrderStatusDTO> getStatus(@PathVariable final String id) {
        return workOrderService.findById(WorkOrderID.from(id))
                .map(WorkOrderMapper::toStatusDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private List<WorkOrderService.PartSelectionInput> mapPartInputs(final List<WorkOrderPartInputDTO> parts) {
        if (parts == null) {
            return List.of();
        }
        return parts.stream()
                .map(item -> new WorkOrderService.PartSelectionInput(item.partId(), item.quantity()))
                .toList();
    }

    @GetMapping("/{id}/average-execution-time")
    public ResponseEntity<Double> getAverageExecutionTime(@PathVariable final String id) {
        double minutes = workOrderService.calculateExecutionTime(WorkOrderID.from(id));
        return ResponseEntity.ok(minutes);
    }
}
