package com.github.soillux.exception.handler;

import com.github.soillux.exception.custom.UserAlreadyExistsException;
import com.github.soillux.util.ErrorResponseBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.github.soillux.util.ErrorResponseBuilder.parseValidationExceptionMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Map<String, String>> handleUserAlreadyExists(RuntimeException exception) {
    Map<String, String> errorResponse = ErrorResponseBuilder.build(
        exception.getMessage(),
        HttpStatus.CONFLICT);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException exception) {
    Map<String, String> errorResponse = ErrorResponseBuilder.build(
        exception.getMessage(),

        HttpStatus.NOT_FOUND);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, Object> errorResponse = ErrorResponseBuilder.build(
        parseValidationExceptionMessage(ex),
        HttpStatus.BAD_REQUEST);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
