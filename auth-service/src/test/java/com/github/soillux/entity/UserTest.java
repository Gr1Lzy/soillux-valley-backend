package com.github.soillux.entity;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

import static com.github.soillux.entity.EnumRole.ROLE_USER;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class UserTest {

  @Test
  void init_should_addUserRole_when_call() {
    // Given
    User user = new User();

    // When
    user.init();

    // Then
    assertThat(user.getRoles()).hasSize(1);
    assertThat(user.getRoles()).extracting(Role::getName)
        .containsExactly(ROLE_USER);
  }

  @Test
  void getAuthorities_should_returnRolesSet_when_call() {
    // Given
    User user = new User();
    user.setRoles(Set.of(Role.of(ROLE_USER)));

    // When & Then
    assertThat(user.getAuthorities()).hasSize(1);
    assertThat(user.getAuthorities()).extracting(GrantedAuthority::getAuthority)
        .containsExactly(String.valueOf(EnumRole.ROLE_USER));
  }
}
