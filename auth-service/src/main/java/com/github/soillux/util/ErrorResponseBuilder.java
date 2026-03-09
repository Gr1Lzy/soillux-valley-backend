package com.github.soillux.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class ErrorResponseBuilder {

  private static final String MESSAGE = "message";
  private static final String STATUS = "status";

  public static Map<String, String> build(String message, HttpStatus status) {
    return Map.of(
        MESSAGE, message,
        STATUS, String.valueOf(status)
    );
  }

  public static Map<String, Object> build(Map<String, String> message, HttpStatus status) {
    return Map.of(
        MESSAGE, message,
        STATUS, String.valueOf(status)
    );
  }

  public static Map<String, String> parseValidationExceptionMessage(MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            error -> error.getDefaultMessage() != null
                ? error.getDefaultMessage()
                : "Invalid value",
            (existing, replacement) -> existing + "; " + replacement
        ));
  }
}