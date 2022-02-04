package com.sriracha.ChuibboServer.model.dto.request.user;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class SignInRequestDto {

    private String email;
    private String password;

}
