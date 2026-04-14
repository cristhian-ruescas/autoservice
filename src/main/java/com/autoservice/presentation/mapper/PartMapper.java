package com.autoservice.presentation.mapper;

import com.autoservice.domain.part.Part;
import com.autoservice.presentation.dto.PartDTO;

public class PartMapper {

    private PartMapper() {
    }

    public static PartDTO toDTO(final Part part) {
        return new PartDTO(
                part.getId().getValue(),
                part.getName(),
                part.getQuantity(),
                part.getUnitPrice()
        );
    }
}

