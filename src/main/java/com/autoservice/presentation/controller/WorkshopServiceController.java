package com.autoservice.presentation.controller;

import com.autoservice.application.WorkshopServiceApplicationService;
import com.autoservice.domain.service.WorkshopService;
import com.autoservice.domain.service.WorkshopServiceID;
import com.autoservice.presentation.dto.CreateWorkshopServiceRequest;
import com.autoservice.presentation.dto.UpdateWorkshopServiceRequest;
import com.autoservice.presentation.dto.WorkshopServiceDTO;
import com.autoservice.presentation.mapper.WorkshopServiceMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
public class WorkshopServiceController {

    private final WorkshopServiceApplicationService workshopServiceApplicationService;

    public WorkshopServiceController(final WorkshopServiceApplicationService workshopServiceApplicationService) {
        this.workshopServiceApplicationService = workshopServiceApplicationService;
    }

    @PostMapping
    public ResponseEntity<WorkshopServiceDTO> create(@Valid @RequestBody final CreateWorkshopServiceRequest request) {
        final WorkshopService workshopService = workshopServiceApplicationService.create(
                request.nome(),
                request.descricao(),
                request.precoBase()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(WorkshopServiceMapper.toDTO(workshopService));
    }

    @GetMapping
    public ResponseEntity<List<WorkshopServiceDTO>> findAll() {
        final List<WorkshopServiceDTO> services = workshopServiceApplicationService.findAll().stream()
                .map(WorkshopServiceMapper::toDTO)
                .toList();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkshopServiceDTO> findById(@PathVariable final String id) {
        return workshopServiceApplicationService.findById(WorkshopServiceID.from(id))
                .map(WorkshopServiceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkshopServiceDTO> update(@PathVariable final String id, @Valid @RequestBody final UpdateWorkshopServiceRequest request) {
        final WorkshopService workshopService = workshopServiceApplicationService.update(
                WorkshopServiceID.from(id),
                request.nome(),
                request.descricao(),
                request.precoBase()
        );
        return ResponseEntity.ok(WorkshopServiceMapper.toDTO(workshopService));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        workshopServiceApplicationService.delete(WorkshopServiceID.from(id));
        return ResponseEntity.noContent().build();
    }
}

