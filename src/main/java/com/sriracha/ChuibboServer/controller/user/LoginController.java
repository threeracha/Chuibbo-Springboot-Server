package com.sriracha.ChuibboServer.controller.user;

import com.sriracha.ChuibboServer.common.Header;
import com.sriracha.ChuibboServer.service.user.login.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user/login")
public class LoginController {

    private final LoginService loginService;

    // 로그인
    @PostMapping("/")
    public Header<String> login(@RequestBody Map<String, String> user) {
        return loginService.login(user);
    }

    @RequestMapping(value = "/kakao", produces = "application/json", method = RequestMethod.GET)
    public Header<String> Kakao(@RequestBody String access_token) throws IOException {
        return loginService.Kakao(access_token);
    }

    @RequestMapping(value = "/google", produces = "application/json", method = RequestMethod.GET)
    public Header<String> Google(@RequestBody String access_token) throws IOException {
        return loginService.Google(access_token);
    }

    @RequestMapping(value = "/naver", produces = "application/json", method = RequestMethod.GET)
    public Header<String> Naver(@RequestBody String access_token) throws IOException {
        return loginService.Naver(access_token);
    }

}
