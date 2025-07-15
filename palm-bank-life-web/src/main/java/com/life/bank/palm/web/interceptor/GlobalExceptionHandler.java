package com.life.bank.palm.web.interceptor;

import com.life.bank.palm.common.exception.BaseBizCodeEnum;
import com.life.bank.palm.common.exception.CommonBizException;
import com.life.bank.palm.common.result.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CommonBizException.class)
    @ResponseBody
    public CommonResponse<Object> bizExceptionHandler(HttpServletRequest req, CommonBizException e) {
        log.warn("业务异常：{}", e.getMessage());
        return CommonResponse.buildError(e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResponse<Object> exceptionHandler(HttpServletRequest req, Exception e) {
        log.error("系统异常：", e);
        return CommonResponse.buildError(BaseBizCodeEnum.SYSTEM_ERROR);
    }
}