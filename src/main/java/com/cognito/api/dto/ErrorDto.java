package com.cognito.api.dto;

import org.springframework.http.HttpStatus;

public record ErrorDto(HttpStatus status, String error) {
}

