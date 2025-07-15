package com.life.bank.palm.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommonBizException extends RuntimeException {
    private int errorCode;
    private String errorMsg;

    public CommonBizException(String errorMsg) {
        super(errorMsg);
        this.errorCode = -1;
        this.errorMsg = errorMsg;
    }

    public CommonBizException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public CommonBizException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getErrorMsg());
        this.errorCode = errorCodeEnum.getErrorCode();
        this.errorMsg = errorCodeEnum.getErrorMsg();
    }
}