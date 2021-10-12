package com.sriracha.ChuibboServer.model.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SocialResponseDto
 *
 * @author jy
 * @version 1.0
 * @See None
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginResponseDto {

    String email;

    String nickname;

}
