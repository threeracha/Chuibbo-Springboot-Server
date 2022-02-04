package com.sriracha.ChuibboServer.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInResponseDto {

    private Long id;
    private String email;
    private String nickname;
    private String accessToken;
    private String refreshToken;

    @Builder
    public SignInResponseDto(Long id, String email, String nickname, String accessToken, String refreshToken) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}