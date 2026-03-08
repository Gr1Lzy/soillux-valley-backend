package com.github.soillux.dto.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserResponseDtoTest {

  @Test
  void should_createDto_when_validData() {
    // Given
    UserResponseDto dto = new UserResponseDto("test@example.com", "testUser");

    // When & Then
    assertThat(dto).isNotNull();
    assertThat(dto.email()).isEqualTo("test@example.com");
    assertThat(dto.username()).isEqualTo("testUser");
  }
}