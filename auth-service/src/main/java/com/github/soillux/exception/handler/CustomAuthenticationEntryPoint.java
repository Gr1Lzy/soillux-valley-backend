package com.github.soillux.exception.handler;

import com.github.soillux.util.ErrorResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(@NonNull HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    Map<String, String> errorResponse = ErrorResponseBuilder.build(
        authException.getMessage(),
        HttpStatus.UNAUTHORIZED);

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}

