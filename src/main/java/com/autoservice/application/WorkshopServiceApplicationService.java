package com.autoservice.application;

import com.autoservice.domain.service.WorkshopService;
import com.autoservice.domain.service.WorkshopServiceID;
import com.autoservice.domain.service.WorkshopServiceRepository;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WorkshopServiceApplicationService {

    private final WorkshopServiceRepository workshopServiceRepository;

    public WorkshopServiceApplicationService(final WorkshopServiceRepository workshopServiceRepository) {
        this.workshopServiceRepository = workshopServiceRepository;
    }

    @Transactional
    public WorkshopService create(final String name, final String description, final BigDecimal basePrice) {
        final WorkshopService workshopService = WorkshopService.newService(name, description, basePrice);
        final ValidationHandler handler = new ThrowsValidationHandler();
        workshopService.validate(handler);
        return workshopServiceRepository.save(workshopService);
    }

    public Optional<WorkshopService> findById(final WorkshopServiceID id) {
        return workshopServiceRepository.findById(id);
    }

    public List<WorkshopService> findAll() {
        return workshopServiceRepository.findAll();
    }

    @Transactional
    public WorkshopService update(final WorkshopServiceID id, final String name, final String description, final BigDecimal basePrice) {
        if (!workshopServiceRepository.existsById(id)) {
            throw new IllegalArgumentException("Service not found");
        }
        final WorkshopService workshopService = WorkshopService.withId(id, name, description, basePrice);
        final ValidationHandler handler = new ThrowsValidationHandler();
        workshopService.validate(handler);
        return workshopServiceRepository.save(workshopService);
    }

    @Transactional
    public void delete(final WorkshopServiceID id) {
        if (!workshopServiceRepository.existsById(id)) {
            throw new IllegalArgumentException("Service not found");
        }
        workshopServiceRepository.deleteById(id);
    }
}

