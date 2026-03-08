package com.github.soillux.dto.auth;

public record LoginResponseDto(

    String accessToken,

    String refreshToken
) {
}
