package com.tomaszekem.userservice.config;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

class ErrorResponse {

    private final String code;
    private final String message;
    private final Map<String, List<String>> validationErrors;

    private ErrorResponse(HttpStatus status, String message, Map<String, List<String>> validationErrors) {
        this.code = status.toString();
        this.message = message;
        this.validationErrors = validationErrors;
    }

    public static ErrorResponse error(HttpStatus status) {
        return ErrorResponse.error(status, status.getReasonPhrase());
    }

    public static ErrorResponse error(HttpStatus status, String message) {
        return new ErrorResponse(status, message, emptyMap());
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, List<String>> getValidationErrors() {
        return validationErrors;
    }
}
