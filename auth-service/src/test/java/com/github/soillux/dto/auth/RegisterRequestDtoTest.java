package com.github.soillux.dto.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestDtoTest {

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
    RegisterRequestDto registerRequestDto = new RegisterRequestDto(
        "test",
        "test@test.com",
        "12345678");

    // When
    Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);

    // Then
    assertThat(violations).isEmpty();
    assertThat(registerRequestDto.username()).isEqualTo("test");
    assertThat(registerRequestDto.email()).isEqualTo("test@test.com");
    assertThat(registerRequestDto.password()).isEqualTo("12345678");
  }

  @Test
  void should_failValidation_when_invalidEmail() {
    // Given
    RegisterRequestDto registerRequestDto = new RegisterRequestDto(
        "test",
        "testtest.com",
        "12345678");

    // When
    Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);

    // Then
    assertThat(violations).isNotEmpty();
  }

  @Test
  void should_failValidation_when_shortPassword() {
    // Given
    RegisterRequestDto registerRequestDto = new RegisterRequestDto(
        "test",
        "test@test.com",
        "1234567");

    // When
    Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);

    // Then
    assertThat(violations).isNotEmpty();
  }

  @Test
  void should_failValidation_when_emptyFields() {
    // Given
    RegisterRequestDto registerRequestDto = new RegisterRequestDto(
        "",
        "",
        "");

    // When
    Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequestDto);

    // Then
    assertThat(violations).isNotEmpty();
  }
}
