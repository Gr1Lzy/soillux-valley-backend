package com.github.soillux.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

  private static final String[] ALLOWED_ORIGINS = {};
  private static final String[] ALLOWED_METHOD = {};
  private static final String[] ALLOWED_HEADERS = {};

  @Value("${server.servlet.context-path}")
  private String registeredCorsPath;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(ALLOWED_ORIGINS));
    configuration.setAllowedMethods(List.of(ALLOWED_METHOD));
    configuration.setAllowedHeaders(List.of(ALLOWED_HEADERS));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(registeredCorsPath + "/**", configuration);

    return source;
  }
}
