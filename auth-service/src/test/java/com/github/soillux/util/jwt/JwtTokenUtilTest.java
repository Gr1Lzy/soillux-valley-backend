package com.github.soillux.util.jwt;

import com.github.soillux.entity.EnumRole;
import com.github.soillux.entity.Role;
import com.github.soillux.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = JwtTokenUtil.class)
class JwtTokenUtilTest {

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  private User testUser;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenLifetime", Duration.ofMinutes(15));
    ReflectionTestUtils.setField(jwtTokenUtil, "refreshTokenLifetime", Duration.ofHours(1));

    testUser = new User();
    ReflectionTestUtils.setField(testUser, "id", 1L);
    testUser.setEmail("test@example.com");
    testUser.setUsername("testuser");
    testUser.setPassword("password");

    Role role = new Role();
    role.setName(EnumRole.ROLE_USER);
    testUser.setRoles(Set.of(role));
  }

  @Test
  void generateAccessToken_should_returnToken_when_validUser() {
    // Given
    String token = jwtTokenUtil.generateAccessToken(testUser);

    // When & Then
    assertThat(token)
        .isNotEmpty()
        .contains(".")
        .hasSizeGreaterThan(50);
    assertThat(jwtTokenUtil.isAccessTokenValid(token)).isTrue();
    assertThat(jwtTokenUtil.extractUserId(token)).isEqualTo(1L);
  }

  @Test
  void generateRefreshToken_should_returnToken_when_validUser() {
    // Given
    String token = jwtTokenUtil.generateRefreshToken(testUser);

    // When & Then
    assertThat(token)
        .isNotEmpty()
        .contains(".")
        .hasSizeGreaterThan(50);
    assertThat(jwtTokenUtil.isRefreshTokenValid(token)).isTrue();
    assertThat(jwtTokenUtil.extractUserId(token)).isEqualTo(1L);
  }

  @Test
  void extractUserId_should_returnUserId_when_validToken() {
    // Given
    String token = jwtTokenUtil.generateAccessToken(testUser);

    // When
    Long userId = jwtTokenUtil.extractUserId(token);

    // Then
    assertThat(userId)
        .isNotNull()
        .isEqualTo(1L)
        .isPositive();
  }

  @Test
  void extractUserRoles_should_returnRoles_when_validAccessToken() {
    // Given
    String token = jwtTokenUtil.generateAccessToken(testUser);

    // When
    List<String> roles = jwtTokenUtil.extractUserRoles(token);

    // Then
    assertThat(roles)
        .isNotNull()
        .isNotEmpty()
        .hasSize(1)
        .containsExactly("ROLE_USER")
        .doesNotContain("ROLE_ADMIN");
  }

  @Test
  void isAccessTokenValid_should_returnTrue_when_validAccessToken() {
    // Given
    String token = jwtTokenUtil.generateAccessToken(testUser);

    // When
    boolean isValid = jwtTokenUtil.isAccessTokenValid(token);

    // Then
    assertThat(isValid).isTrue();
    assertThat(jwtTokenUtil.isRefreshTokenValid(token)).isFalse();
    assertThat(jwtTokenUtil.extractUserId(token)).isEqualTo(1L);
  }

  @Test
  void isAccessTokenValid_should_returnFalse_when_refreshToken() {
    // Given
    String token = jwtTokenUtil.generateRefreshToken(testUser);

    // When
    boolean isValid = jwtTokenUtil.isAccessTokenValid(token);

    // Then
    assertThat(isValid).isFalse();
    assertThat(jwtTokenUtil.isRefreshTokenValid(token)).isTrue();
    assertThat(token).isNotEmpty();
  }

  @Test
  void isRefreshTokenValid_should_returnTrue_when_validRefreshToken() {
    // Given
    String token = jwtTokenUtil.generateRefreshToken(testUser);

    // When
    boolean isValid = jwtTokenUtil.isRefreshTokenValid(token);

    // Then
    assertThat(isValid).isTrue();
    assertThat(jwtTokenUtil.isAccessTokenValid(token)).isFalse();
    assertThat(jwtTokenUtil.extractUserId(token)).isEqualTo(1L);
  }

  @Test
  void isRefreshTokenValid_should_returnFalse_when_accessToken() {
    // Given
    String token = jwtTokenUtil.generateAccessToken(testUser);

    // When
    boolean isValid = jwtTokenUtil.isRefreshTokenValid(token);

    // Then
    assertThat(isValid).isFalse();
    assertThat(jwtTokenUtil.isAccessTokenValid(token)).isTrue();
    assertThat(token).isNotEmpty();
  }

  @Test
  void isAccessTokenValid_should_returnFalse_when_invalidToken() {
    // Given
    boolean isValid = jwtTokenUtil.isAccessTokenValid("invalid.token.here");

    // When & Then
    assertThat(isValid).isFalse();
    assertThat(jwtTokenUtil.isRefreshTokenValid("invalid.token.here")).isFalse();
    assertThat(jwtTokenUtil.isAccessTokenValid("")).isFalse();
    assertThat(jwtTokenUtil.isAccessTokenValid("a.b.c")).isFalse();
  }

  @Test
  void generateAccessToken_should_throwException_when_notUserInstance() {
    // Given
    UserDetails mockUser = mock(UserDetails.class);
    when(mockUser.getUsername()).thenReturn("test");
    when(mockUser.getAuthorities()).thenReturn(Collections.emptyList());

    // When & Then
    assertThatThrownBy(() -> jwtTokenUtil.generateAccessToken(mockUser))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("UserDetails must be an instance of User");
  }

  @Test
  void isAccessTokenValid_should_returnFalse_when_tokenExpired() {
    // Given
    ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenLifetime", Duration.ofMillis(1));
    String token = jwtTokenUtil.generateAccessToken(testUser);

    // When
    await().atMost(Duration.ofSeconds(2)).until(() -> !jwtTokenUtil.isAccessTokenValid(token));

    // Then
    assertThat(jwtTokenUtil.isAccessTokenValid(token)).isFalse();
    assertThat(token).isNotEmpty();
  }

  @Test
  void isAccessTokenValid_should_returnTrue_when_tokenNotExpired() {
    // Given
    ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenLifetime", Duration.ofHours(1));
    String token = jwtTokenUtil.generateAccessToken(testUser);

    // When
    boolean isValid = jwtTokenUtil.isAccessTokenValid(token);

    // Then
    assertThat(isValid).isTrue();
    assertThat(token).isNotEmpty();
  }

  @Test
  void isAccessTokenValid_should_returnFalse_when_tokenExpiredAndCaught() {
    // Given
    ReflectionTestUtils.setField(jwtTokenUtil, "accessTokenLifetime", Duration.ofMillis(1));
    String token = jwtTokenUtil.generateAccessToken(testUser);

    // When
    await().atMost(Duration.ofSeconds(2)).until(() -> !jwtTokenUtil.isAccessTokenValid(token));
    boolean isValid = jwtTokenUtil.isAccessTokenValid(token);

    // Then
    assertThat(isValid).isFalse();
    assertThat(token).isNotEmpty();
  }
}
