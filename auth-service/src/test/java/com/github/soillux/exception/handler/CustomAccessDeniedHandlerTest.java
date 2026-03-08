package com.github.soillux.exception.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @InjectMocks
  private CustomAccessDeniedHandler handler;

  @Test
  void handle_should_returnForbidden_when_accessDenied() throws Exception {
    // Given
    AccessDeniedException exception = new AccessDeniedException("Access Denied");

    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(writer);
    when(objectMapper.writeValueAsString(anyMap()))
        .thenReturn("{\"status\":\"403 FORBIDDEN\",\"message\":\"Access Denied\"}");

    // When
    handler.handle(request, response, exception);

    // Then
    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response).setContentType("application/json");
    verify(objectMapper).writeValueAsString(anyMap());

    writer.flush();
    assertThat(stringWriter.toString()).contains("403 FORBIDDEN");
  }
}
