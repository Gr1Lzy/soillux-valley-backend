package com.github.soillux.mapper;

import com.github.soillux.dto.auth.RegisterRequestDto;
import com.github.soillux.dto.user.UserResponseDto;
import com.github.soillux.entity.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

  private static final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

  @Test
  void toEntity_should_mapCorrectly_when_validRegisterRequestDto() {
    // Given
    RegisterRequestDto requestDto = new RegisterRequestDto(
        "testuser",
        "test@test.com",
        "password123"
    );

    // When
    User user = userMapper.toEntity(requestDto);

    // Then
    assertThat(user).satisfies(u -> {
      assertThat(u.getUsername()).isEqualTo("testuser");
      assertThat(u.getEmail()).isEqualTo("test@test.com");
      assertThat(u.getPassword()).isEqualTo("password123");
      assertThat(u.getRoles()).isEmpty();
    });
  }

  @Test
  void toEntity_should_returnNull_when_nullRegisterRequestDto() {
    // Given & When
    User user = userMapper.toEntity(null);

    // Then
    assertThat(user).isNull();
  }

  @Test
  void toDto_should_mapCorrectly_when_validUser() {
    // Given
    User user = new User();
    user.setUsername("testuser");
    user.setEmail("test@test.com");
    user.setPassword("password123");

    // When
    UserResponseDto dto = userMapper.toDto(user);

    // Then
    assertThat(dto).satisfies(d -> {
      assertThat(d.username()).isEqualTo("testuser");
      assertThat(d.email()).isEqualTo("test@test.com");
    });
  }

  @Test
  void toDto_should_returnNull_when_nullUser() {
    // Given & When
    UserResponseDto dto = userMapper.toDto(null);

    // Then
    assertThat(dto).isNull();
  }
}
