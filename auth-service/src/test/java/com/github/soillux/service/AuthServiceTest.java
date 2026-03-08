package com.github.soillux.service;

import com.github.soillux.dto.auth.LoginRequestDto;
import com.github.soillux.dto.auth.LoginResponseDto;
import com.github.soillux.dto.auth.RegisterRequestDto;
import com.github.soillux.entity.EnumRole;
import com.github.soillux.entity.Role;
import com.github.soillux.entity.User;
import com.github.soillux.exception.custom.AuthenticationException;
import com.github.soillux.exception.custom.UserAlreadyExistsException;
import com.github.soillux.mapper.UserMapper;
import com.github.soillux.repository.UserRepository;
import com.github.soillux.service.impl.AuthServiceImpl;
import com.github.soillux.util.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserMapper userMapper;

  @Mock
  private JwtTokenUtil jwtTokenUtil;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void register_should_saveUser_when_validRequest() {
    // Given
    RegisterRequestDto requestDto = new RegisterRequestDto(
        "test@example.com",
        "testuser",
        "password123");
    User user = new User();
    user.setUsername("testuser");
    when(userMapper.toEntity(requestDto)).thenReturn(user);
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);

    // When
    authService.register(requestDto);

    // Then
    verify(userMapper).toEntity(requestDto);
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(user);
    assertThat(user.getPassword()).isEqualTo("encodedPassword");
  }

  @Test
  void register_should_throwException_when_duplicateUser() {
    // Given
    RegisterRequestDto requestDto = new RegisterRequestDto(
        "test@example.com",
        "testuser",
        "password123");
    User user = new User();
    when(userMapper.toEntity(requestDto)).thenReturn(user);
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

    // When & Then
    assertThatThrownBy(() -> authService.register(requestDto))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessage("Email or username already registered");
  }

  @Test
  void login_should_returnTokens_when_validCredentials() {
    // Given
    User user = new User();
    user.setUsername("testuser");
    user.setRoles(Set.of(Role.of(EnumRole.ROLE_USER)));
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(user);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
    when(jwtTokenUtil.generateAccessToken(user)).thenReturn("accessToken");
    when(jwtTokenUtil.generateRefreshToken(user)).thenReturn("refreshToken");
    LoginRequestDto requestDto = new LoginRequestDto("testuser", "password123");

    // When
    LoginResponseDto response = authService.login(requestDto);

    // Then
    assertThat(response.accessToken()).isEqualTo("accessToken");
    assertThat(response.refreshToken()).isEqualTo("refreshToken");
    verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
  }

  @Test
  void login_should_throwException_when_invalidCredentials() {
    // Given
    LoginRequestDto requestDto = new LoginRequestDto("testuser", "wrongpassword");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new RuntimeException("Bad credentials"));

    // When & Then
    assertThatThrownBy(() -> authService.login(requestDto))
        .isInstanceOf(AuthenticationException.class)
        .hasMessage("Invalid username or password");
  }

  @Test
  void login_should_throwException_when_invalidPrincipal() {
    // Given
    LoginRequestDto requestDto = new LoginRequestDto("testuser", "password123");
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn("notAUser");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

    // When & Then
    assertThatThrownBy(() -> authService.login(requestDto))
        .isInstanceOf(AuthenticationException.class)
        .hasMessage("Invalid authentication principal");
  }
}
