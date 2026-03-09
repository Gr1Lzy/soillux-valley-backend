package com.github.soillux.mapper;

import com.github.soillux.config.MapStructConfig;
import com.github.soillux.dto.auth.RegisterRequestDto;
import com.github.soillux.dto.user.UserResponseDto;
import com.github.soillux.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

  User toEntity(RegisterRequestDto requestDto);

  UserResponseDto toDto(User user);
}
