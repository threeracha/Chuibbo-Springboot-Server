/**
 * LoginUserAuditorAware
 * DATE를 자동으로 주입
 *
 * @author jy
 * @version 1.0
 * @see None
 */
package com.sriracha.ChuibboServer.common.utils;

import com.sriracha.ChuibboServer.model.enumclass.Role;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LoginUserAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(Role.ADMIN.getKey());
    }

}
