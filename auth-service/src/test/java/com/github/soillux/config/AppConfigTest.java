package com.github.soillux.config;

import com.github.soillux.entity.User;
import com.github.soillux.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppConfigTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AppConfig appConfig;

  @Test
  void userDetailsService_should_returnUser_when_usernameExists() {
    // Given
    User mockUser = mock(User.class);
    when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

    // When
    UserDetailsService serviceDetails = appConfig.userDetailsService();
    UserDetails result = serviceDetails.loadUserByUsername("testUser");

    // Then
    assertThat(result).satisfies(user -> {
      assertThat(user).isNotNull();
      assertThat(user).isEqualTo(mockUser);
    });
    verify(userRepository).findByUsername("testUser");
  }

  @Test
  void userDetailsService_should_throwException_when_usernameNotFound() {
    // Given
    when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
    UserDetailsService service = appConfig.userDetailsService();

    // When & Then
    assertThatThrownBy(() -> service.loadUserByUsername("unknown"))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage("User not found");
  }
}
