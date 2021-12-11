/**
 * Header
 * request 및 respose header
 *
 * @author jy
 * @version 1.0
 * @see None
 */
package com.sriracha.ChuibboServer.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**@JsonInclude 어떠한 값들만 포함시킬 것인지 -> 추후 추가*/
public class Header<T> { // Generic
    //snake case

    // api 통신시간
    // @JsonProperty("transaction_time") -> application.properties에 설정
    private LocalDateTime transactionTime;

    // api 응답코드
    private String resultCode;

    // api 부가설명
    private String description;

    // 데이터
    private T data;

    // 토큰
    private T accessToken;
    private T refreshToken;

    // OK
    public static <T> Header<T> OK(){
        return(Header<T>)Header.builder()
                .transactionTime(LocalDateTime.now())
                .resultCode("OK")
                .description("OK")
                .build();
    }

    // DATA OK
    public static <T> Header<T> OK(T data){
        return(Header<T>)Header.builder()
                .transactionTime(LocalDateTime.now())
                .resultCode("DATA OK")
                .description("DATA OK")
                .data(data)
                .build();
    }

    // ERROR
    public static <T> Header<T> ERROR(String description){
        return(Header<T>)Header.builder()
                .transactionTime(LocalDateTime.now())
                .resultCode("ERROR")
                .description(description)
                .build();
    }

    public static <T> Header<T> TOKEN(T accessToken, T refreshToken) {
        return(Header<T>)Header.builder()
                .transactionTime(LocalDateTime.now())
                .resultCode("DATA OK")
                .description("DATA OK")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

