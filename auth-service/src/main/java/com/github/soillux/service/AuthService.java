package com.github.soillux.service;

import com.github.soillux.dto.auth.LoginRequestDto;
import com.github.soillux.dto.auth.LoginResponseDto;
import com.github.soillux.dto.auth.RegisterRequestDto;

public interface AuthService {

  void register(RegisterRequestDto requestDto);

  LoginResponseDto login(LoginRequestDto requestDto);
}
