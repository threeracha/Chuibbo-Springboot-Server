package com.sriracha.ChuibboServer.service.user;

import com.sriracha.ChuibboServer.common.Header;
import com.sriracha.ChuibboServer.common.jwt.JwtTokenProvider;
import com.sriracha.ChuibboServer.model.dto.response.user.EmailResponseDto;
import com.sriracha.ChuibboServer.model.dto.response.user.UserResponseDto;
import com.sriracha.ChuibboServer.model.entity.User;
import com.sriracha.ChuibboServer.model.enumclass.Role;
import com.sriracha.ChuibboServer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;

    public Header signup(Map<String, String> user) {
        // TODO 아이디 중복 불가처리?
        userRepository.save(User.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .nickname(user.get("nickname"))
                .role(Role.USER) // 최초 가입시 USER 로 설정
                .build()).getId();
        return Header.OK();
    }

    // TODO 유효기간 계산해서 Redis에서 삭제하기 -> memory 공간을 쓸데없이 차지하기 때문
    @Transactional
    public Header logout(String jwt) {
        // TODO RedisUtil의 setDataExpire 로 바꾸기
        ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
        logoutValueOperations.set(jwt, jwt); // TODO key 를 jwt로 사용?....
        User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();
        log.info("로그아웃 유저 아이디 : '{}' , 유저 이름 : '{} ", user.getEmail(), user.getNickname());
        return Header.OK("logout success");
    }

    // 사용자 정보 : email, nickname
    public Header<UserResponseDto> userInfo(String jwt) {
        User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();

        return userRepository.findByEmail(user.getEmail())
                .map(member -> response(member))
                .orElseGet(() -> Header.ERROR("no data"));
    }

    // 임시 비밀번호 발급
    public Header sendEmail(String email) {
        EmailResponseDto dto = emailService.createMailAndChangePassword(email);
        emailService.sendEmail(dto);
        return Header.OK();
    }

    // 비밀번호 변경
    public Header changePw(String jwt, String newPassword) {
        User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();

        return userRepository.findByEmail(user.getEmail())
                .map(member -> {
                    member.setPassword(passwordEncoder.encode(newPassword));
                    return member;
                })
                .map(changeUserPw -> userRepository.save(changeUserPw))
                .map(newUser -> response(newUser))
                .orElseGet(() -> Header.ERROR("패스워드 변경 실패"));
    }

    // 탈퇴
    public Header withdraw(String jwt) {

        User user = (User) jwtTokenProvider.getAuthentication(jwt).getPrincipal();

        Optional<User> optional = userRepository.findById(user.getId());

        return optional.map(member -> {
            userRepository.delete(member);
            return Header.OK();
        }).orElseGet(() -> Header.ERROR("no data"));
    }

    public Header checkNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .map(member -> Header.ERROR("이미 존재하는 닉네임 입니다.")).orElseGet(() -> Header.OK("닉네임 사용할 수 있습니다."));
    }

    public Header checkEmail(String email) {
        return userRepository.findByEmail(email)
                .map(member -> Header.ERROR("이미 존재하는 아이디 입니다.")).orElseGet(() -> Header.OK("아이디를 사용할 수 있습니다."));
    }

    private Header<UserResponseDto> response(User user) {
        UserResponseDto userResponseDto = UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();

        return Header.OK(userResponseDto);
    }
}
