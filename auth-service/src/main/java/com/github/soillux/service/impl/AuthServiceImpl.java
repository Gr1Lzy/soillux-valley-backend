package com.github.soillux.service.impl;

import com.github.soillux.dto.auth.LoginRequestDto;
import com.github.soillux.dto.auth.LoginResponseDto;
import com.github.soillux.dto.auth.RegisterRequestDto;
import com.github.soillux.entity.User;
import com.github.soillux.exception.custom.AuthenticationException;
import com.github.soillux.exception.custom.UserAlreadyExistsException;
import com.github.soillux.mapper.UserMapper;
import com.github.soillux.repository.UserRepository;
import com.github.soillux.service.AuthService;
import com.github.soillux.util.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserMapper userMapper;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Override
  public void register(RegisterRequestDto requestDto) {
    log.info("Registration attempt for username: {}", requestDto.username());

    User user = userMapper.toEntity(requestDto);
    user.setPassword(passwordEncoder.encode(requestDto.password()));
    user.init();

    try {
      userRepository.save(user);
      log.info("User successfully registered: {}", requestDto.username());
    } catch (DataIntegrityViolationException _) {
      log.warn("Registration failed: duplicate email or username");
      throw new UserAlreadyExistsException("Email or username already registered");
    }
  }

  @Override
  public LoginResponseDto login(LoginRequestDto requestDto) {
    log.info("Login attempt for username: {}", requestDto.username());

    String username = requestDto.username();
    String password = requestDto.password();

    Authentication authentication = tryToAuthenticate(username, password);

    if (!(authentication.getPrincipal() instanceof User user)) {
      throw new AuthenticationException("Invalid authentication principal");
    }

    return new LoginResponseDto(
        jwtTokenUtil.generateAccessToken(user),
        jwtTokenUtil.generateRefreshToken(user));
  }

  private Authentication tryToAuthenticate(String username, String password) {
    try {
      return authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(username, password));
    } catch (Exception _) {
      throw new AuthenticationException("Invalid username or password");
    }
  }
}
