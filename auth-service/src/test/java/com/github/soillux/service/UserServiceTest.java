package com.github.soillux.service;

import com.github.soillux.dto.user.UserResponseDto;
import com.github.soillux.entity.User;
import com.github.soillux.mapper.UserMapper;
import com.github.soillux.repository.UserRepository;
import com.github.soillux.service.impl.UserServiceImpl;
import com.github.soillux.util.UserUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private static final Long USER_ID = 1L;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  private MockedStatic<UserUtil> userUtilMock;

  @BeforeEach
  void setUp() {
    userUtilMock = mockStatic(UserUtil.class);
    userUtilMock.when(UserUtil::getCurrentUserId).thenReturn(USER_ID);
  }

  @AfterEach
  void tearDown() {
    userUtilMock.close();
  }

  @Test
  void getCurrentUser_should_returnUserDto_when_userExists() {
    // Given
    User user = new User();
    user.setUsername("testuser");
    user.setEmail("test@example.com");

    UserResponseDto expectedDto = new UserResponseDto("test@example.com", "testuser");

    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(expectedDto);

    // When
    UserResponseDto result = userService.getCurrentUser();

    // Then
    assertThat(result).isEqualTo(expectedDto);
    verify(userRepository).findById(USER_ID);
    verify(userMapper).toDto(user);
    userUtilMock.verify(UserUtil::getCurrentUserId);
  }

  @Test
  void getCurrentUser_should_throwException_when_userNotFound() {
    // Given
    when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.getCurrentUser())
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("User not found");

    verify(userMapper, never()).toDto(any());
  }
}