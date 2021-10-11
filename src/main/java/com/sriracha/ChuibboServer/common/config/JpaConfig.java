/**
 * JpaConfig
 * JPA 설정을 위한 Configuration
 *
 * @author jy
 * @version 1.0
 * @see None
 */

package com.sriracha.ChuibboServer.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {

}