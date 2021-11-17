package com.sriracha.ChuibboServer.controller.user;

import com.sriracha.ChuibboServer.common.Header;
import com.sriracha.ChuibboServer.model.dto.request.user.SignUpRequestDto;
import com.sriracha.ChuibboServer.model.dto.response.user.UserResponseDto;
import com.sriracha.ChuibboServer.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @ApiOperation("회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.signup(signUpRequestDto));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // TODO : response 타입 바꾸기
        }
    }

    @GetMapping("/info")
    public Header<UserResponseDto> userInfo(@RequestHeader("Authorization") String jwt) {
        return userService.userInfo(jwt);
    }

    // TODO 유효기간 계산해서 Redis에서 삭제하기 -> memory 공간을 쓸데없이 차지하기 때문
    @Transactional
    @GetMapping(value = "/logout")
    public Header logout(@RequestHeader("Authorization") String jwt) {
        return userService.logout(jwt);
    }

    @PostMapping("/find-password")
    public Header sendEmail(@RequestBody String email){
        return userService.sendEmail(email);
    }

    @PutMapping("/change-password")
    public Header changePw(@RequestHeader("Authorization") String jwt, @RequestBody String newPassword){
        return userService.changePw(jwt, newPassword);
    }

    @DeleteMapping("/withdraw")
    public Header withdraw(@RequestHeader("Authorization") String jwt) {
        return userService.withdraw(jwt);
    }

    @GetMapping("/checkNickname")
    public Header checkNickname(@RequestParam String nickname) { return userService.checkNickname(nickname); }

    @GetMapping("/checkEmail")
    public Header checkEmail(@RequestParam String email) { return userService.checkEmail(email); }

    @GetMapping("/checkValidation")
    public boolean checkValidation(@RequestHeader("Authorization") String jwt) {
        return userService.checkValidation(jwt);
    }
}
