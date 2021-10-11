package com.sriracha.ChuibboServer.common.exception;

import lombok.Getter;

@Getter
public class BaseException {

    protected ErrorCode error;

    protected BaseException(ErrorCode error) { this.error = error; }
}
