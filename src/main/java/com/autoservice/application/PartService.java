package com.autoservice.application;

import com.autoservice.domain.part.Part;
import com.autoservice.domain.part.PartID;
import com.autoservice.domain.part.PartRepository;
import com.autoservice.validation.ValidationHandler;
import com.autoservice.validation.handler.ThrowsValidationHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PartService {

    private final PartRepository partRepository;

    public PartService(final PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Transactional
    public Part create(final String name, final Integer quantity, final BigDecimal unitPrice) {
        final Part part = Part.newPart(name, quantity, unitPrice);
        final ValidationHandler handler = new ThrowsValidationHandler();
        part.validate(handler);
        return partRepository.save(part);
    }

    public Optional<Part> findById(final PartID id) {
        return partRepository.findById(id);
    }

    public List<Part> findAll() {
        return partRepository.findAll();
    }

    @Transactional
    public Part update(final PartID id, final String name, final Integer quantity, final BigDecimal unitPrice) {
        if (!partRepository.existsById(id)) {
            throw new IllegalArgumentException("Part not found");
        }

        final Part part = Part.withId(id, name, quantity, unitPrice);
        final ValidationHandler handler = new ThrowsValidationHandler();
        part.validate(handler);
        return partRepository.save(part);
    }

    @Transactional
    public void delete(final PartID id) {
        if (!partRepository.existsById(id)) {
            throw new IllegalArgumentException("Part not found");
        }
        partRepository.deleteById(id);
    }
}

