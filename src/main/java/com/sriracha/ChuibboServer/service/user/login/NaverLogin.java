package com.sriracha.ChuibboServer.service.user.login;

/**
 * NaverLogin
 *
 * @author jy
 * @version 1.0
 * @See None
 */

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sriracha.ChuibboServer.model.dto.response.user.SocialLoginResponseDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 네이버 user response sample
 * {
 * "resultcode": "00",
 * "message": "success",
 * "response": {
 * "email": "openapi@naver.com",
 * "nickname": "OpenAPI",
 * "profile_image": "https://ssl.pstatic.net/static/pwe/address/nodata_33x33.gif",
 * "age": "40-49",
 * "gender": "F",
 * "id": "32742776",
 * "name": "오픈 API",
 * "birthday": "10-01",
 * "birthyear": "1900",
 * "mobile": "010-0000-0000"
 * }
 * }
 */

public class NaverLogin {

    public SocialLoginResponseDto getUserInfo(String access_token) {

        SocialLoginResponseDto socialLoginResponseDto = null;

        String header = "Bearer " + access_token; // Bearer 다음에 공백 추가

        String apiURL = "https://openapi.naver.com/v1/nid/me";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Authorization", header);
        String responseBody = get(apiURL, requestHeaders);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(responseBody);

        // TODO 안의 요소 바꾸기
        JsonObject properties = element.getAsJsonObject().get("response").getAsJsonObject();

        String nickname = properties.getAsJsonObject().get("nickname").getAsString();
        String email = properties.getAsJsonObject().get("email").getAsString();

        socialLoginResponseDto = SocialLoginResponseDto.builder().email(email).nickname(nickname).build();

        System.out.println(responseBody);

        return socialLoginResponseDto;
    }

    private String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
