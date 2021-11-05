package com.sriracha.ChuibboServer.common.responseEntity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message<T> {

    private LocalDateTime transactionTime;
    private StatusEnum status;
    private String message;
    private T data;

    public Message() {
        this.transactionTime = LocalDateTime.now();
        this.status = StatusEnum.BAD_REQUEST;
        this.data = null;
        this.message = null;
    }
}