package com.sriracha.ChuibboServer.model.dto.request.user;

import com.sriracha.ChuibboServer.model.entity.User;
import com.sriracha.ChuibboServer.model.enumclass.Role;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@Getter
@Builder
public class SignUpRequestDto {

    private String email;
    private String password;
    private String nickname;

    public User toEntity() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return User.builder().email(email).nickname(nickname).password(passwordEncoder.encode(password)).role(Role.USER).build();
    }

}
