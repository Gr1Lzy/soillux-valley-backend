package com.github.soillux.dto.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestDtoTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  @Test
  void should_createDto_when_validData() {
    // Given
    LoginRequestDto dto = new LoginRequestDto("testuser", "password123");

    // When
    Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

    // Then
    assertThat(violations).isEmpty();
  }

  @Test
  void should_failValidation_when_emptyUsername() {
    // Given
    LoginRequestDto dto = new LoginRequestDto("", "password123");

    // When
    Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

    // Then
    assertThat(violations).isNotEmpty();
  }

  @Test
  void should_failValidation_when_emptyPassword() {
    // Given
    LoginRequestDto dto = new LoginRequestDto("testuser", "");

    // When
    Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(dto);

    // Then
    assertThat(violations).isNotEmpty();
  }
}
