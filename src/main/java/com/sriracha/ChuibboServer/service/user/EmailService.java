/**
 * SendEmailService
 * 임시 비밀번호를 가입한 이메일로 전송한다.
 *
 * @author jy
 * @version 1.0
 * @see None
 */
package com.sriracha.ChuibboServer.service.user;

import com.sriracha.ChuibboServer.model.dto.response.user.EmailResponseDto;
import com.sriracha.ChuibboServer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private static String FROM_ADDRESS;

    // TODO change temporary password info to image asset
    public EmailResponseDto createMailAndChangePassword(String email) {
        String str = getTempPassword();
        EmailResponseDto dto = new EmailResponseDto();
        dto.setAddress(email);
        dto.setTitle(email + "님의 Chuibbo 임시비밀번호 안내 이메일 입니다.");
        dto.setMessage("안녕하세요. Chuibbo 임시비밀번호 안내 관련 이메일 입니다. " + "[" + email + "] " + "님의 임시 비밀번호는 "
                + "[ "+ str + " ] " + "입니다.");
        updatePassword(str, email);
        return dto;
    }

    // TODO add orElseThrow
    public void updatePassword(String str, String email) {
        String password = passwordEncoder.encode(str);
        System.out.println(password);
        userRepository.findByEmail(email)
                .map(item -> {
                    item.setPassword(password);
                    return item;
                }).map(newItem -> userRepository.save(newItem));
    }


    public String getTempPassword() {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        String str = "";

        int idx = 0;
        for (int i = 0; i < 15; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        System.out.println(str);
        return str;
    }

    // TODO 이메일 전송 실패시 try-catch 문으로 감싸기
    public void sendEmail(EmailResponseDto dto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getAddress());
        message.setFrom(EmailService.FROM_ADDRESS);
        message.setSubject(dto.getTitle());
        message.setText(dto.getMessage());

        mailSender.send(message);
    }
}