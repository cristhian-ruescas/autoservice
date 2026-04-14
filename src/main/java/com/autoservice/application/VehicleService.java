package com.autoservice.application;

import com.autoservice.domain.customer.Customer;
import com.autoservice.domain.customer.CustomerID;
import com.autoservice.domain.customer.CustomerRepository;
import com.autoservice.domain.vehicle.Vehicle;
import com.autoservice.domain.vehicle.VehicleID;
import com.autoservice.domain.vehicle.VehicleRepository;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;

    public VehicleService(final VehicleRepository vehicleRepository, final CustomerRepository customerRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Vehicle create(final String plate, final String brand, final String model, final Integer year, final String customerId) {
        final Customer customer = customerRepository.findById(CustomerID.from(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        final Vehicle vehicle = Vehicle.newVehicle(plate, brand, model, year, customer);
        final ValidationHandler handler = new ThrowsValidationHandler();
        vehicle.validate(handler);
        return vehicleRepository.save(vehicle);
    }

    public Optional<Vehicle> findById(final VehicleID id) {
        return vehicleRepository.findById(id);
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Transactional
    public Vehicle update(final VehicleID id, final String plate, final String brand, final String model, final Integer year, final String customerId) {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("Vehicle not found");
        }

        final Customer customer = customerRepository.findById(CustomerID.from(customerId))
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        final Vehicle vehicle = Vehicle.withId(id, plate, brand, model, year, customer);
        final ValidationHandler handler = new ThrowsValidationHandler();
        vehicle.validate(handler);
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public void delete(final VehicleID id) {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("Vehicle not found");
        }
        vehicleRepository.deleteById(id);
    }
}

