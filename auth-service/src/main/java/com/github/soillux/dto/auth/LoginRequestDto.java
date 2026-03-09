package com.github.soillux.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(

    @Schema(example = "user")
    @NotBlank(message = "Username is required")
    String username,

    @Schema(example = "12345678")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {
}
