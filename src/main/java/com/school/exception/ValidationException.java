package com.school.exception;

public class ValidationException extends RuntimeException {

    private String fieldName;
    private String message;

    public ValidationException(String message) {
        super(message);
        this.message = message;
    }

    public ValidationException(String fieldName, String message) {
        super(String.format("Validation error on field '%s': %s", fieldName, message));
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
