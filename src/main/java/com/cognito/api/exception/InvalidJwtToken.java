package com.cognito.api.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidJwtToken extends RuntimeException {
    private HttpStatus status;
    private String message;
    InvalidJwtToken(HttpStatus status, String message){
        super(message);
        this.status = status;
        this.message = message;
    }
}
