package com.example.accessing_data_rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED, reason="Condition for player joining is not met")
public class CouldNotCreatePlayerException extends RuntimeException {
    public CouldNotCreatePlayerException(String message) {
        super(message);
    }

}
