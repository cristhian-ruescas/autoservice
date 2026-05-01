package com.autoservice.presentation.controller;

import com.autoservice.application.WorkOrderService;
import com.autoservice.domain.customer.Customer;
import com.autoservice.domain.customer.CustomerID;
import com.autoservice.domain.vehicle.Vehicle;
import com.autoservice.domain.vehicle.VehicleID;
import com.autoservice.domain.workorder.WorkOrder;
import com.autoservice.domain.workorder.WorkOrderID;
import com.autoservice.domain.workorder.WorkOrderStatus;
import com.autoservice.presentation.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WorkOrderControllerTest {
    private WorkOrderController workOrderController;
    private FakeWorkOrderService fakeWorkOrderService;

    @BeforeEach
    void setUp() {
        fakeWorkOrderService = new FakeWorkOrderService();
        fakeWorkOrderService.createdWorkOrder = new WorkOrderStub();
        fakeWorkOrderService.updatedWorkOrder = new WorkOrderStub();
        fakeWorkOrderService.updatedStatusWorkOrder = new WorkOrderStub();
        fakeWorkOrderService.workOrders = List.of(new WorkOrderStub());
        fakeWorkOrderService.foundWorkOrder = Optional.of(new WorkOrderStub());
        workOrderController = new WorkOrderController(fakeWorkOrderService);
    }

    @Test
    void testCreate() {
        CreateWorkOrderRequest request = new CreateWorkOrderRequest("1", "2", List.of("3"), List.of());
        ResponseEntity<WorkOrderDTO> response = workOrderController.create(request);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testFindAll() {
        ResponseEntity<List<WorkOrderDTO>> response = workOrderController.findAll();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testFindByIdFound() {
        ResponseEntity<WorkOrderDTO> response = workOrderController.findById("1");
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testFindByIdNotFound() {
        fakeWorkOrderService.foundWorkOrder = Optional.empty();
        ResponseEntity<WorkOrderDTO> response = workOrderController.findById("1");
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testUpdate() {
        UpdateWorkOrderRequest request = new UpdateWorkOrderRequest("1", "2", List.of("3"), List.of());
        ResponseEntity<WorkOrderDTO> response = workOrderController.update("1", request);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdateStatus() {
        UpdateWorkOrderStatusRequest request = new UpdateWorkOrderStatusRequest(WorkOrderStatus.COMPLETED);
        ResponseEntity<WorkOrderDTO> response = workOrderController.updateStatus("1", request);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetStatusFound() {
        ResponseEntity<WorkOrderStatusDTO> response = workOrderController.getStatus("1");
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetStatusNotFound() {
        fakeWorkOrderService.foundWorkOrder = Optional.empty();
        ResponseEntity<WorkOrderStatusDTO> response = workOrderController.getStatus("1");
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testGetAverageExecutionTime() {
        fakeWorkOrderService.executionTime = 42.0;
        ResponseEntity<Double> response = workOrderController.getAverageExecutionTime("1");
        assertEquals(200, response.getStatusCode().value());
        assertEquals(42.0, response.getBody());
    }

    static class FakeWorkOrderService extends WorkOrderService {
        public WorkOrder createdWorkOrder;
        public List<WorkOrder> workOrders = new ArrayList<>();
        public Optional<WorkOrder> foundWorkOrder = Optional.empty();
        public WorkOrder updatedWorkOrder;
        public WorkOrder updatedStatusWorkOrder;
        public double executionTime = 0.0;

        public FakeWorkOrderService() {
            super(null, null, null, null, null);
        }

        @Override
        public WorkOrder create(String customerId, String vehicleId, List<String> serviceIds, List<PartSelectionInput> parts) {
            return createdWorkOrder != null ? createdWorkOrder : new WorkOrderStub();
        }

        @Override
        public List<WorkOrder> findAll() {
            return workOrders.isEmpty() ? List.of(new WorkOrderStub()) : workOrders;
        }

        @Override
        public Optional<WorkOrder> findById(WorkOrderID id) {
            return foundWorkOrder;
        }

        @Override
        public WorkOrder update(WorkOrderID id, String customerId, String vehicleId, List<String> serviceIds, List<PartSelectionInput> parts) {
            return updatedWorkOrder != null ? updatedWorkOrder : new WorkOrderStub();
        }

        @Override
        public WorkOrder updateStatus(WorkOrderID id, WorkOrderStatus status) {
            return updatedStatusWorkOrder != null ? updatedStatusWorkOrder : new WorkOrderStub();
        }

        @Override
        public double calculateExecutionTime(WorkOrderID workOrderId) {
            return executionTime;
        }
    }

    static class WorkOrderStub extends WorkOrder {
        private final Customer stubCustomer;

        public WorkOrderStub() {
            super();
            stubCustomer = new Customer() {
                @Override
                public CustomerID getId() {
                    return CustomerID.from("test-customer-id");
                }
            };
            try {
                java.lang.reflect.Field customerField = WorkOrder.class.getDeclaredField("customer");
                customerField.setAccessible(true);
                customerField.set(this, stubCustomer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public WorkOrderID getId() {
            return WorkOrderID.unique();
        }

        @Override
        public WorkOrderStatus getStatus() {
            return WorkOrderStatus.COMPLETED;
        }

        @Override
        public Customer getCustomer() {
            return stubCustomer;
        }

        @Override
        public Vehicle getVehicle() {
            Customer customer = stubCustomer;
            return new Vehicle() {
                @Override
                public VehicleID getId() {
                    return VehicleID.from("test-vehicle-id");
                }

                @Override
                public Customer getCustomer() {
                    return customer;
                }
            };
        }
    }
}
