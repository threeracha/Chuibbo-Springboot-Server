package com.sriracha.ChuibboServer.model.enumclass;

/**
 * Provider
 * 사용자 타입
 *
 * @author jy
 * @version 1.0
 * @see None
 */

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {

    KAKAO("KAKAO", "카카오 사용자"),
    GOOGLE("GOOGLE", "구글 사용자"),
    NAVER("NAVER", "네이버 사용자"),
    LOCAL("LOCAL", "로컬 사용자");

    private final String key;
    private final String title;

}
