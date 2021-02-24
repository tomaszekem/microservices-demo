package com.tomaszekem.userservice.config;

import com.tomaszekem.userservice.exception.RequestValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
class ExceptionHandlerConfig {


    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<ErrorResponse> requestValidationException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.error(BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> httpRequestMethodNotSupported(ConstraintViolationException ex) {
        String violationsCollected = ex.getConstraintViolations().stream()
                .map(e -> e.getPropertyPath().toString() + " " + e.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ErrorResponse.error(BAD_REQUEST, violationsCollected));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> generalException(Exception ex) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ErrorResponse.error(INTERNAL_SERVER_ERROR));
    }

}
