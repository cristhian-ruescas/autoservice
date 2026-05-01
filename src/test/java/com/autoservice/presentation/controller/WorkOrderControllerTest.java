package com.autoservice.presentation.controller;

import com.autoservice.application.WorkOrderService;
import com.autoservice.domain.workorder.WorkOrder;
import com.autoservice.domain.workorder.WorkOrderID;
import com.autoservice.domain.workorder.WorkOrderStatus;
import com.autoservice.presentation.dto.*;
import com.autoservice.presentation.mapper.WorkOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WorkOrderControllerTest {
    @Mock
    private WorkOrderService workOrderService;

    @InjectMocks
    private WorkOrderController workOrderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        CreateWorkOrderRequest request = mock(CreateWorkOrderRequest.class);
        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrderService.create(any(), any(), any(), any())).thenReturn(workOrder);
        when(request.customerId()).thenReturn("1");
        when(request.vehicleId()).thenReturn("2");
        when(request.serviceIds()).thenReturn(List.of("3"));
        when(request.parts()).thenReturn(List.of());
        when(workOrder.getId()).thenReturn(WorkOrderID.unique());
        ResponseEntity<WorkOrderDTO> response = workOrderController.create(request);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testFindAll() {
        when(workOrderService.findAll()).thenReturn(List.of(mock(WorkOrder.class)));
        ResponseEntity<List<WorkOrderDTO>> response = workOrderController.findAll();
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testFindByIdFound() {
        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrderService.findById(any())).thenReturn(Optional.of(workOrder));
        ResponseEntity<WorkOrderDTO> response = workOrderController.findById("1");
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testFindByIdNotFound() {
        when(workOrderService.findById(any())).thenReturn(Optional.empty());
        ResponseEntity<WorkOrderDTO> response = workOrderController.findById("1");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testUpdate() {
        UpdateWorkOrderRequest request = mock(UpdateWorkOrderRequest.class);
        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrderService.update(any(), any(), any(), any(), any())).thenReturn(workOrder);
        when(request.customerId()).thenReturn("1");
        when(request.vehicleId()).thenReturn("2");
        when(request.serviceIds()).thenReturn(List.of("3"));
        when(request.parts()).thenReturn(List.of());
        ResponseEntity<WorkOrderDTO> response = workOrderController.update("1", request);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdateStatus() {
        UpdateWorkOrderStatusRequest request = mock(UpdateWorkOrderStatusRequest.class);
        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrderService.updateStatus(any(), any())).thenReturn(workOrder);
        when(request.status()).thenReturn(WorkOrderStatus.FINISHED);
        ResponseEntity<WorkOrderDTO> response = workOrderController.updateStatus("1", request);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetStatusFound() {
        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrderService.findById(any())).thenReturn(Optional.of(workOrder));
        ResponseEntity<WorkOrderStatusDTO> response = workOrderController.getStatus("1");
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetStatusNotFound() {
        when(workOrderService.findById(any())).thenReturn(Optional.empty());
        ResponseEntity<WorkOrderStatusDTO> response = workOrderController.getStatus("1");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetAverageExecutionTime() {
        when(workOrderService.calculateExecutionTime(any())).thenReturn(42.0);
        ResponseEntity<Double> response = workOrderController.getAverageExecutionTime("1");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(42.0, response.getBody());
    }
}

