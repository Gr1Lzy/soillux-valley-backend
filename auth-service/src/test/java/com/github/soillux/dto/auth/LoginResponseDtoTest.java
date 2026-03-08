package com.github.soillux.dto.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LoginResponseDtoTest {

  @Test
  void should_createDto_when_validData() {
    // Given
    LoginResponseDto dto = new LoginResponseDto("accessToken", "refreshToken");

    // When & Then
    assertThat(dto).isNotNull();
    assertThat(dto.accessToken()).isEqualTo("accessToken");
    assertThat(dto.refreshToken()).isEqualTo("refreshToken");
  }
}
