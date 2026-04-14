package com.autoservice.application;

import com.autoservice.domain.customer.Customer;
import com.autoservice.domain.customer.CustomerID;
import com.autoservice.domain.customer.CustomerRepository;
import com.autoservice.domain.part.Part;
import com.autoservice.domain.part.PartID;
import com.autoservice.domain.part.PartRepository;
import com.autoservice.domain.service.WorkshopService;
import com.autoservice.domain.service.WorkshopServiceID;
import com.autoservice.domain.service.WorkshopServiceRepository;
import com.autoservice.domain.vehicle.Vehicle;
import com.autoservice.domain.vehicle.VehicleID;
import com.autoservice.domain.vehicle.VehicleRepository;
import com.autoservice.domain.workorder.WorkOrder;
import com.autoservice.domain.workorder.WorkOrderID;
import com.autoservice.domain.workorder.WorkOrderRepository;
import com.autoservice.domain.workorder.WorkOrderStatus;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final WorkshopServiceRepository workshopServiceRepository;
    private final PartRepository partRepository;

    public WorkOrderService(
            final WorkOrderRepository workOrderRepository,
            final CustomerRepository customerRepository,
            final VehicleRepository vehicleRepository,
            final WorkshopServiceRepository workshopServiceRepository,
            final PartRepository partRepository
    ) {
        this.workOrderRepository = workOrderRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.workshopServiceRepository = workshopServiceRepository;
        this.partRepository = partRepository;
    }

    @Transactional
    public WorkOrder create(
            final String customerId,
            final String vehicleId,
            final List<String> serviceIds,
            final List<PartSelectionInput> partInputs
    ) {
        final Customer customer = customerRepository.findById(CustomerID.from(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        final Vehicle vehicle = vehicleRepository.findById(VehicleID.from(vehicleId))
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        validateVehicleBelongsToCustomer(customer, vehicle);

        final WorkOrder workOrder = WorkOrder.newWorkOrder(customer, vehicle);
        workOrder.replaceServices(resolveServices(serviceIds));
        workOrder.replaceParts(resolveParts(partInputs));

        final ValidationHandler handler = new ThrowsValidationHandler();
        workOrder.validate(handler);

        return workOrderRepository.save(workOrder);
    }

    public Optional<WorkOrder> findById(final WorkOrderID id) {
        return workOrderRepository.findById(id);
    }

    public List<WorkOrder> findAll() {
        return workOrderRepository.findAll();
    }

    @Transactional
    public WorkOrder update(
            final WorkOrderID id,
            final String customerId,
            final String vehicleId,
            final List<String> serviceIds,
            final List<PartSelectionInput> partInputs
    ) {
        final WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found"));

        final Customer customer = customerRepository.findById(CustomerID.from(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        final Vehicle vehicle = vehicleRepository.findById(VehicleID.from(vehicleId))
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        validateVehicleBelongsToCustomer(customer, vehicle);

        workOrder.reassign(customer, vehicle);
        workOrder.replaceServices(resolveServices(serviceIds));
        workOrder.replaceParts(resolveParts(partInputs));

        final ValidationHandler handler = new ThrowsValidationHandler();
        workOrder.validate(handler);

        return workOrderRepository.save(workOrder);
    }

    @Transactional
    public WorkOrder updateStatus(final WorkOrderID id, final WorkOrderStatus status) {
        final WorkOrder workOrder = workOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Work order not found"));

        workOrder.changeStatus(status);
        return workOrderRepository.save(workOrder);
    }

    private List<WorkshopService> resolveServices(final List<String> serviceIds) {
        final List<WorkshopService> services = new ArrayList<>();

        if (serviceIds == null) {
            return services;
        }

        for (String serviceId : serviceIds) {
            final WorkshopService service = workshopServiceRepository.findById(WorkshopServiceID.from(serviceId))
                    .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceId));
            services.add(service);
        }

        return services;
    }

    private List<WorkOrder.PartSelection> resolveParts(final List<PartSelectionInput> partInputs) {
        final List<WorkOrder.PartSelection> parts = new ArrayList<>();

        if (partInputs == null) {
            return parts;
        }

        for (PartSelectionInput partInput : partInputs) {
            if (partInput.quantity() == null || partInput.quantity() <= 0) {
                throw new IllegalArgumentException("Part quantity should be greater than zero");
            }

            final Part part = partRepository.findById(PartID.from(partInput.partId()))
                    .orElseThrow(() -> new IllegalArgumentException("Part not found: " + partInput.partId()));

            parts.add(new WorkOrder.PartSelection(part, partInput.quantity()));
        }

        return parts;
    }

    private void validateVehicleBelongsToCustomer(final Customer customer, final Vehicle vehicle) {
        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Vehicle does not belong to informed customer");
        }
    }

    public record PartSelectionInput(String partId, Integer quantity) {
    }
}


