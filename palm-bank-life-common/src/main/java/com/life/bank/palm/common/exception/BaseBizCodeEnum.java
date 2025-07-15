package com.life.bank.palm.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseBizCodeEnum implements ErrorCodeEnum {
    SUCCESS(0, "OK"),
    SYSTEM_ERROR(-1, "服务器错误"),
    COMMON_ERROR(1001, "参数异常"),
    NOT_LOGIN(999, "未登录");

    private final Integer code;
    private final String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}