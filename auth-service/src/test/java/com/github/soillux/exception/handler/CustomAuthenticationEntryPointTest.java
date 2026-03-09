package com.github.soillux.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @InjectMocks
  private CustomAuthenticationEntryPoint entryPoint;

  @Test
  void commence_should_returnUnauthorized_when_authenticationFails() throws Exception {
    // Given
    AuthenticationException exception = mock(AuthenticationException.class);
    when(exception.getMessage()).thenReturn("Unauthorized");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);
    when(objectMapper.writeValueAsString(anyMap()))
        .thenReturn("{\"status\":\"401 UNAUTHORIZED\",\"message\":\"Unauthorized\"}");

    // When
    entryPoint.commence(request, response, exception);

    // Then
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setContentType("application/json");
    verify(objectMapper).writeValueAsString(anyMap());

    writer.flush();
    assertThat(stringWriter.toString()).contains("401 UNAUTHORIZED");
  }
}

