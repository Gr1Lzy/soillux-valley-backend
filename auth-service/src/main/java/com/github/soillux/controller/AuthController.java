package com.github.soillux.controller;

import com.github.soillux.dto.auth.LoginRequestDto;
import com.github.soillux.dto.auth.LoginResponseDto;
import com.github.soillux.dto.auth.RegisterRequestDto;
import com.github.soillux.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication Endpoints")
@RequestMapping("/v1/auth")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "[Public] Register a new user")
  @PostMapping("/register")
  public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto requestDto) {
    authService.register(requestDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "[Public] User authentication")
  @PostMapping("/login")
  public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
    return ResponseEntity.ok(authService.login(requestDto));
  }
}
