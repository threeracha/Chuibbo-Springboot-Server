package com.sriracha.ChuibboServer.common.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ResponseError {

    @Builder.Default
    private LocalDateTime transactionTime = LocalDateTime.now();

    @Builder.Default
    private StatusEnum status = StatusEnum.BAD_REQUEST;

    private String message;
}
