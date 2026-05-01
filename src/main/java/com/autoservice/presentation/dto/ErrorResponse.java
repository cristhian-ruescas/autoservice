package com.autoservice.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        String path,
        LocalDateTime timestamp,
        List<FieldError> errors
) {
    public record FieldError(
            String field,
            String message
    ) {
    }
}
