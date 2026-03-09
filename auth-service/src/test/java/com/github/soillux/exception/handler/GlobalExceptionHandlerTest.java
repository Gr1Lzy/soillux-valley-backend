package com.github.soillux.exception.handler;

import com.github.soillux.exception.custom.UserAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleUserAlreadyExists_should_returnConflict_when_userExists() {
    // Given
    UserAlreadyExistsException exception = new UserAlreadyExistsException("User already exists");

    // When
    ResponseEntity<Map<String, String>> response = handler.handleUserAlreadyExists(exception);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody())
        .isNotNull()
        .containsEntry("status", "409 CONFLICT")
        .containsEntry("message", "User already exists");
  }

  @Test
  void handleValidationException_should_returnBadRequest_when_validationFails() {
    // Given
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("object", "field", "Field is required");
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

    MethodParameter methodParameter = mock(MethodParameter.class);
    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

    // When
    ResponseEntity<Map<String, Object>> response = handler.handleValidationException(exception);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody())
        .isNotNull()
        .containsEntry("status", "400 BAD_REQUEST")
        .containsKey("message");
  }

  @Test
  void handleValidationException_should_returnInvalidValue_when_noDefaultMessage() {
    // Given
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = mock(FieldError.class);
    when(fieldError.getField()).thenReturn("field");
    when(fieldError.getDefaultMessage()).thenReturn(null);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

    MethodParameter methodParameter = mock(MethodParameter.class);
    MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

    // When
    ResponseEntity<Map<String, Object>> response = handler.handleValidationException(exception);

    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody())
        .isNotNull()
        .containsEntry("status", "400 BAD_REQUEST");

    @SuppressWarnings("unchecked")
    Map<String, String> message = (Map<String, String>) response.getBody().get("message");
    assertThat(message).containsEntry("field", "Invalid value");
  }
}
