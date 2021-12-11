package com.sriracha.ChuibboServer.common.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ResponseData<T> {

    @Builder.Default
    private LocalDateTime transactionTime = LocalDateTime.now();

    @Builder.Default
    private StatusEnum status = StatusEnum.OK;

    @Builder.Default
    private String message = "OK";

    private T data;
}