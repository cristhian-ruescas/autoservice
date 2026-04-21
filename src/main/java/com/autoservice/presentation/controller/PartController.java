package com.autoservice.presentation.controller;

import com.autoservice.application.PartService;
import com.autoservice.domain.part.Part;
import com.autoservice.domain.part.PartID;
import com.autoservice.presentation.dto.CreatePartRequest;
import com.autoservice.presentation.dto.PartDTO;
import com.autoservice.presentation.dto.UpdatePartRequest;
import com.autoservice.presentation.mapper.PartMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parts")
public class PartController {

    private final PartService partService;

    public PartController(final PartService partService) {
        this.partService = partService;
    }

    @PostMapping
    public ResponseEntity<PartDTO> create(@Valid @RequestBody final CreatePartRequest request) {
        final Part part = partService.create(request.nome(), request.quantidade(), request.precoUnitario());
        return ResponseEntity.status(HttpStatus.CREATED).body(PartMapper.toDTO(part));
    }

    @GetMapping
    public ResponseEntity<List<PartDTO>> findAll() {
        final List<PartDTO> parts = partService.findAll().stream().map(PartMapper::toDTO).toList();
        return ResponseEntity.ok(parts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartDTO> findById(@PathVariable final String id) {
        return partService.findById(PartID.from(id))
                .map(PartMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartDTO> update(@PathVariable final String id, @Valid @RequestBody final UpdatePartRequest request) {
        final Part part = partService.update(PartID.from(id), request.nome(), request.quantidade(), request.precoUnitario());
        return ResponseEntity.ok(PartMapper.toDTO(part));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        partService.delete(PartID.from(id));
        return ResponseEntity.noContent().build();
    }
}

