package com.github.soillux.util.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

  @Mock
  private JwtTokenUtil jwtTokenUtil;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @InjectMocks
  private JwtAuthFilter jwtAuthFilter;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_should_continueChain_when_noToken() throws Exception {
    // Given
    when(request.getHeader("Authorization")).thenReturn(null);

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    verify(jwtTokenUtil, never()).isAccessTokenValid(any());
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void doFilterInternal_should_continueChain_when_invalidToken() throws Exception {
    // Given
    when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token");
    when(jwtTokenUtil.isAccessTokenValid("invalid.token")).thenReturn(false);

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    verify(jwtTokenUtil).isAccessTokenValid("invalid.token");
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void doFilterInternal_should_setAuthentication_when_validToken() throws Exception {
    // Given
    String token = "valid.jwt.token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtTokenUtil.isAccessTokenValid(token)).thenReturn(true);
    when(jwtTokenUtil.extractUserId(token)).thenReturn(1L);
    when(jwtTokenUtil.extractUserRoles(token)).thenReturn(List.of("ROLE_USER"));

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    verify(jwtTokenUtil).isAccessTokenValid(token);
    verify(jwtTokenUtil).extractUserId(token);
    verify(jwtTokenUtil).extractUserRoles(token);

    assertThat(SecurityContextHolder.getContext().getAuthentication())
        .isNotNull()
        .satisfies(auth -> {
          assertThat(Objects.requireNonNull(auth).getPrincipal()).isEqualTo(1L);
          assertThat(auth.getAuthorities()).hasSize(1);
          assertThat(auth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        });
  }

  @Test
  void doFilterInternal_should_notSetAuthentication_when_alreadyAuthenticated() throws Exception {
    // Given
    String token = "valid.jwt.token";
    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtTokenUtil.isAccessTokenValid(token)).thenReturn(true);

    SecurityContextHolder.getContext().setAuthentication(
        new org.springframework.security.authentication.UsernamePasswordAuthenticationToken("user", null));

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    verify(jwtTokenUtil).isAccessTokenValid(token);
    verify(jwtTokenUtil, never()).extractUserId(any());
    verify(jwtTokenUtil, never()).extractUserRoles(any());
  }

  @Test
  void doFilterInternal_should_continueChain_when_noBearerPrefix() throws Exception {
    // Given
    when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    verify(jwtTokenUtil, never()).isAccessTokenValid(any());
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }
}

