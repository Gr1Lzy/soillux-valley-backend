package com.github.soillux.controller;

import com.github.soillux.dto.user.UserResponseDto;
import com.github.soillux.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Endpoints")
@RequestMapping("/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;

  @Operation(summary = "[Authenticated] Get current user info")
  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getCurrentUser() {
    return ResponseEntity.ok(userService.getCurrentUser());
  }
}
