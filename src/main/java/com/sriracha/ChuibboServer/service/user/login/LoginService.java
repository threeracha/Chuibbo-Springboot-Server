package com.sriracha.ChuibboServer.service.user.login;

/**
 * SocialLoginService
 *
 * @author jy
 * @version 1.0
 * @See None
 */

import com.sriracha.ChuibboServer.common.Header;
import com.sriracha.ChuibboServer.common.jwt.JwtTokenProvider;
import com.sriracha.ChuibboServer.common.utils.RedisUtil;
import com.sriracha.ChuibboServer.model.dto.response.user.SocialLoginResponseDto;
import com.sriracha.ChuibboServer.model.entity.User;
import com.sriracha.ChuibboServer.model.enumclass.Role;
import com.sriracha.ChuibboServer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RedisUtil redisUtil;

    private KakaoLogin kakaoLogin;
    private GoogleLogin googleLogin;
    private NaverLogin naverLogin;

    public Header<String> login(Map<String, String> user) {
        try {
            User member = userRepository.findByEmail(user.get("email"))
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
            if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
                throw new IllegalArgumentException("잘못된 비밀번호입니다.");
            }
            final String accessJwt = jwtTokenProvider.createToken(member.getId(), Role.USER, member.getEmail());
            final String refreshJwt = jwtTokenProvider.createRefreshToken(member.getId(), Role.USER, member.getEmail());
            // TODO getUsername() -> email을 반환함 User가 UserDetail를 implement 하지 않는 방법?...
            redisUtil.setDataExpire(refreshJwt, member.getUsername(), JwtTokenProvider.REFRESH_TOKEN_VALIDATION_SECOND);
            return Header.TOKEN(accessJwt, refreshJwt);
        }catch (Exception e){
            log.info("error" + e);
            return Header.ERROR("로그인에 실패하셨습니다");
        }
    }


    public Header<String> Kakao(String access_token) throws IOException {
        SocialLoginResponseDto userInfo = kakaoLogin.getUserInfo(access_token);
        // TODO 코드 정리 - findByEmailandprovider 로 바꾸기, 회원가입 + 로그인 동
        User member = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        return Header.TOKEN(jwtTokenProvider.createToken(member.getId(), Role.USER, member.getEmail()),
                jwtTokenProvider.createRefreshToken(member.getId(), Role.USER, member.getEmail()));
    }

    public Header<String> Google(String access_token) throws IOException {
        SocialLoginResponseDto userInfo = googleLogin.getUserInfo(access_token);
        // TODO 코드 정리 - findByEmailandProvider 로 바꾸기, 회원가입 + 로그인 동시
        User member = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIl"));
        return Header.TOKEN(jwtTokenProvider.createToken(member.getId(), Role.USER, member.getEmail()),
                jwtTokenProvider.createRefreshToken(member.getId(), Role.USER, member.getEmail()));
    }

    public Header<String> Naver(String access_token) throws IOException {
        SocialLoginResponseDto userInfo = naverLogin.getUserInfo(access_token);
        // TODO 코드 정리 - findByEmailandProvider 로 바꾸기
        Optional<User> member = userRepository.findByEmail(userInfo.getEmail());
        if(!member.isPresent()) {
            User newUser = User.builder()
                    .email(userInfo.getEmail())
                    .nickname(userInfo.getNickname())
                    .role(Role.USER) // 최초 가입시 USER 로 설정
                    .build();
            userRepository.save(newUser);
            // 사용자에게서 받은 정보를 바탕으로 토큰 발행 -> DB에 저장된 걸 다시 불러온게 아님
            // TODO 조회하지 말고 그냥 가져다 쓰기
            return Header.TOKEN(jwtTokenProvider.createToken(newUser.getId(), Role.USER, newUser.getEmail()),
                    jwtTokenProvider.createRefreshToken(newUser.getId(), Role.USER, newUser.getEmail()));
        }
        // TODO 존재하는 경우 : optional을 최적으로 사용하는 방법?
        return Header.TOKEN(jwtTokenProvider.createToken(member.get().getId(), Role.USER, member.get().getEmail()),
                jwtTokenProvider.createRefreshToken(member.get().getId(), Role.USER, member.get().getEmail()));
    }
}
