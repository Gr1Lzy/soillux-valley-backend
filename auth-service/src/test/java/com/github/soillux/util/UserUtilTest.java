package com.github.soillux.util;

import com.github.soillux.exception.custom.AuthenticationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserUtilTest {

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void getCurrentUserId_should_returnUserId_when_authenticationIsValid() {
    // Given
    Long userId = 123L;
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(userId);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // When
    Long result = UserUtil.getCurrentUserId();

    // Then
    assertThat(result).isEqualTo(userId);
  }

  @Test
  void getCurrentUserId_should_throwException_when_authenticationIsNull() {
    // Given
    SecurityContextHolder.clearContext();

    // When & Then
    assertThatThrownBy(UserUtil::getCurrentUserId)
        .isInstanceOf(AuthenticationException.class)
        .hasMessage("Anonymous user is not allowed to access this resource");
  }

  @Test
  void getCurrentUserId_should_throwException_when_userIsAnonymous() {
    // Given
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("anonymousUser");
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // When & Then
    assertThatThrownBy(UserUtil::getCurrentUserId)
        .isInstanceOf(AuthenticationException.class)
        .hasMessage("Anonymous user is not allowed to access this resource");
  }

  @Test
  void getCurrentUserId_should_returnNull_when_principalIsNull() {
    // Given
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // When
    Long result = UserUtil.getCurrentUserId();

    // Then
    assertThat(result).isNull();
  }

}
