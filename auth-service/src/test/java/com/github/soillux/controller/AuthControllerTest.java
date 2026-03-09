package com.github.soillux.controller;

import com.github.soillux.dto.auth.LoginRequestDto;
import com.github.soillux.dto.auth.LoginResponseDto;
import com.github.soillux.dto.auth.RegisterRequestDto;
import com.github.soillux.exception.custom.UserAlreadyExistsException;
import com.github.soillux.exception.handler.GlobalExceptionHandler;
import com.github.soillux.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthController authController;

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(authController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .setValidator(new LocalValidatorFactoryBean())
        .build();


    objectMapper = new ObjectMapper();
  }

  @Test
  void register_should_returnStatusCreate_when_validData() throws Exception {
    // Given
    RegisterRequestDto requestDto = new RegisterRequestDto(
        "test", "test@test.con", "12345678");
    String jsonObject = objectMapper.writeValueAsString(requestDto);

    // When & Then
    mockMvc.perform(post("/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject))
        .andExpect(status().isCreated());
  }

  @Test
  void register_should_returnAlreadyCreated_when_sameData() throws Exception {
    // Given
    RegisterRequestDto requestDto = new RegisterRequestDto(
            "test", "test@test.com", "12345678");
    String jsonObject = objectMapper.writeValueAsString(requestDto);

    // When
    doThrow(new UserAlreadyExistsException("User already exists"))
        .when(authService)
        .register(any(RegisterRequestDto.class));

    // Then
    mockMvc.perform(post("/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject))
        .andExpect(status().isConflict());
  }

  @Test
  void register_should_returnInvalidResponse_when_invalidData() throws Exception {
    // Given
    RegisterRequestDto requestDto = new RegisterRequestDto(
        "", "", "");
    String jsonObject = objectMapper.writeValueAsString(requestDto);

    // When & Then
    mockMvc.perform(post("/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonObject))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").isMap())
        .andExpect(jsonPath("$.message.username").exists())
        .andExpect(jsonPath("$.message.email").exists())
        .andExpect(jsonPath("$.message.password").exists())
        .andExpect(jsonPath("$.status").value("400 BAD_REQUEST"));
  }

  @Test
  void login_should_returnResponse_when_validData() throws Exception {
    // Given
    LoginRequestDto requestDto = new LoginRequestDto("test", "12345678");
    String jsonObject = objectMapper.writeValueAsString(requestDto);

    // When
    when(authService.login(any(LoginRequestDto.class)))
        .thenReturn(new LoginResponseDto("accessToken", "refreshToken"));

    // Then
    mockMvc.perform(post("/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonObject))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }
}