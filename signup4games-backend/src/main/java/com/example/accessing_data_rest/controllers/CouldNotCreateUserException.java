package com.example.accessing_data_rest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED, reason="Condition for creating user is not met")
public class CouldNotCreateUserException extends RuntimeException {
    public CouldNotCreateUserException(String message) {
        super(message);
    }

}
