package com.sriracha.ChuibboServer.service.user.login;

/** 
 * GoogleLoginService
 *
 * @author jy
 * @version 1.0
 * @see None
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sriracha.ChuibboServer.model.dto.response.user.SocialLoginResponseDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleLogin {

    public SocialLoginResponseDto getUserInfo (String access_token) {

        SocialLoginResponseDto socialLoginResponseDto = null;

        // TODO 원래 있는 사용자인지 확인하고 userRepository 에 저장하기 -> 회원가입은 첫 1회만 한다.
        String reqURL = "https://www.googleapis.com/oauth2/v2/userinfo"; // TODO 여기 바꿔야됨
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            // 요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String nickname = element.getAsJsonObject().get("name").getAsString();
            String email = element.getAsJsonObject().get("email").getAsString();

            socialLoginResponseDto =  SocialLoginResponseDto.builder().email(email).nickname(nickname).build();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return socialLoginResponseDto;
    }
}
