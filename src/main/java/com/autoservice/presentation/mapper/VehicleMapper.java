package com.autoservice.presentation.mapper;

import com.autoservice.domain.vehicle.Vehicle;
import com.autoservice.presentation.dto.VehicleDTO;

public class VehicleMapper {

    private VehicleMapper() {
    }

    public static VehicleDTO toDTO(final Vehicle vehicle) {
        return new VehicleDTO(
                vehicle.getId().getValue(),
                vehicle.getPlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getCustomer().getId().getValue()
        );
    }
}

