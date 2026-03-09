package com.github.soillux.controller;

import com.github.soillux.dto.user.UserResponseDto;
import com.github.soillux.exception.handler.GlobalExceptionHandler;
import com.github.soillux.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController userController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(userController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void getCurrentUser_should_returnCurrentUser_when_call() throws Exception {
    // Given
    when(userService.getCurrentUser()).thenReturn(new UserResponseDto("test@test.com", "test"));

    // When & Then
    mockMvc.perform(get("/v1/users/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("test@test.com"))
        .andExpect(jsonPath("$.username").value("test"));
  }

  @Test
  void getCurrentUser_should_throwEntityNotFound_when_notFoundById() throws Exception {
    // Given
    when(userService.getCurrentUser()).thenThrow(new EntityNotFoundException("User not found"));

    // When & Then
    mockMvc.perform(get("/v1/users/me"))
        .andExpect(status().isNotFound());
  }
}