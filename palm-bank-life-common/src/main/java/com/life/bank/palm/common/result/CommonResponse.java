package com.life.bank.palm.common.result;

import com.life.bank.palm.common.exception.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommonResponse<T> {
    private boolean success;
    private int code;
    private String msg;
    private T data;

    public static <T> CommonResponse<T> buildSuccess() {
        return new CommonResponse<>(true, 0, "OK", null);
    }

    public static <T> CommonResponse<T> buildSuccess(T data) {
        return new CommonResponse<>(true, 0, "OK", data);
    }

    public static <T> CommonResponse<T> buildError(String errorMsg) {
        return new CommonResponse<>(false, -1, errorMsg, null);
    }

    public static <T> CommonResponse<T> buildError(int errorCode, String errorMsg) {
        return new CommonResponse<>(false, errorCode, errorMsg, null);
    }

    // 添加这个方法来支持 ErrorCodeEnum
    public static <T> CommonResponse<T> buildError(ErrorCodeEnum errorCodeEnum) {
        return new CommonResponse<>(false, errorCodeEnum.getErrorCode(), errorCodeEnum.getErrorMsg(), null);
    }
}