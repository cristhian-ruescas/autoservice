package com.autoservice.presentation.controller;

import com.autoservice.application.VehicleService;
import com.autoservice.domain.vehicle.Vehicle;
import com.autoservice.domain.vehicle.VehicleID;
import com.autoservice.presentation.dto.CreateVehicleRequest;
import com.autoservice.presentation.dto.UpdateVehicleRequest;
import com.autoservice.presentation.dto.VehicleDTO;
import com.autoservice.presentation.mapper.VehicleMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(final VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<VehicleDTO> create(@Valid @RequestBody final CreateVehicleRequest request) {
        final Vehicle vehicle = vehicleService.create(
                request.plate(),
                request.brand(),
                request.model(),
                request.year(),
                request.customerId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(VehicleMapper.toDTO(vehicle));
    }

    @GetMapping
    public ResponseEntity<List<VehicleDTO>> findAll() {
        final List<VehicleDTO> vehicles = vehicleService.findAll().stream().map(VehicleMapper::toDTO).toList();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDTO> findById(@PathVariable final String id) {
        return vehicleService.findById(VehicleID.from(id))
                .map(VehicleMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDTO> update(@PathVariable final String id, @Valid @RequestBody final UpdateVehicleRequest request) {
        final Vehicle vehicle = vehicleService.update(
                VehicleID.from(id),
                request.plate(),
                request.brand(),
                request.model(),
                request.year(),
                request.customerId()
        );
        return ResponseEntity.ok(VehicleMapper.toDTO(vehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        vehicleService.delete(VehicleID.from(id));
        return ResponseEntity.noContent().build();
    }
}

