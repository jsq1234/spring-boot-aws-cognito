package com.cognito.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ConfirmEmailRequest(
    @NotBlank(message="Email cannot be blank") String email,
    @NotBlank(message="Confirmation code cannot be blank") String code
) {
    
}
