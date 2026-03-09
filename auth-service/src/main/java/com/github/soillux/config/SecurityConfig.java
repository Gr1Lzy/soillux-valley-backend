package com.github.soillux.config;

import com.github.soillux.exception.handler.CustomAccessDeniedHandler;
import com.github.soillux.exception.handler.CustomAuthenticationEntryPoint;
import com.github.soillux.util.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String[] PUBLIC_ENDPOINTS = {
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/v3/api-docs/**",

      "/v1/auth/**"
  };

  private static final String[] USER_ENDPOINTS = {
      "/v1/users/**"
  };

  private static final String[] ADMIN_ENDPOINTS = {

  };

  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final CustomAccessDeniedHandler accessDeniedHandler;
  private final JwtAuthFilter jwtAuthFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)

        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .authorizeHttpRequests(request -> request
            .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
            .requestMatchers(USER_ENDPOINTS).hasRole("USER"))

        .exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))

        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }
}
