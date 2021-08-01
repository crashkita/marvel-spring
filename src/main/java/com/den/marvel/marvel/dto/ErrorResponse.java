package com.den.marvel.marvel.dto;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

public class ErrorResponse {
    List<String> errors;

    public ErrorResponse(ConstraintViolationException constraintViolationException) {
        errors = constraintViolationException.getConstraintViolations()
                .stream()
                .map(c -> c.getMessage())
                .collect(Collectors.toList());
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
