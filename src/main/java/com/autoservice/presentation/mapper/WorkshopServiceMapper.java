package com.autoservice.presentation.mapper;

import com.autoservice.domain.service.WorkshopService;
import com.autoservice.presentation.dto.WorkshopServiceDTO;

public class WorkshopServiceMapper {

    private WorkshopServiceMapper() {
    }

    public static WorkshopServiceDTO toDTO(final WorkshopService workshopService) {
        return new WorkshopServiceDTO(
                workshopService.getId().getValue(),
                workshopService.getName(),
                workshopService.getDescription(),
                workshopService.getBasePrice()
        );
    }
}

