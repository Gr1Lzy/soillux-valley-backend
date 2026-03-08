package com.github.soillux.service.impl;

import com.github.soillux.dto.user.UserResponseDto;
import com.github.soillux.entity.User;
import com.github.soillux.mapper.UserMapper;
import com.github.soillux.repository.UserRepository;
import com.github.soillux.service.UserService;
import com.github.soillux.util.UserUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserResponseDto getCurrentUser() {
    Long userId = UserUtil.getCurrentUserId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return userMapper.toDto(user);
  }
}
