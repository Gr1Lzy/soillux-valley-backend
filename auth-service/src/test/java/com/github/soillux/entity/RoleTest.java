package com.github.soillux.entity;

import org.junit.jupiter.api.Test;

import static com.github.soillux.entity.EnumRole.ROLE_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoleTest {

  @Test
  void of_should_returnRole_when_call() {
    // Given
    Role role = new Role();
    role.setName(ROLE_USER);

    // Whenw
    Role.of(ROLE_USER);

    // Then
    assertNotNull(role.getName());
    assertThat(role.getAuthority()).isEqualTo("ROLE_USER");
  }

  @Test
  void getAuthority_should_returnNull_when_callWithIncorrectName() {
    // Given
    Role role = Role.of(null);

    // When
    String actual = role.getAuthority();

    // Then
    assertThat(actual).isEqualTo("ROLE_ANONYMOUS");
  }

  @Test
  void getAuthority_should_returnAuthority_when_call() {
    // Given
    Role role = Role.of(ROLE_USER);

    // When
    String actual = role.getAuthority();

    // Then
    assertThat(actual).isEqualTo("ROLE_USER");
  }
}
