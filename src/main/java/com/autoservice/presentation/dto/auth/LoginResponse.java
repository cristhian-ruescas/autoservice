package com.autoservice.presentation.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponse", description = "JWT para Authorization: Bearer.")
public record LoginResponse(
        @Schema(description = "Token JWT")
        String token
) {
}
