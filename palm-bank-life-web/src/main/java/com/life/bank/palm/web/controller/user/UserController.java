package com.life.bank.palm.web.controller.user;

import com.life.bank.palm.common.result.CommonResponse;
import com.life.bank.palm.service.user.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserLoginService userLoginService;  // 添加这行注入服务

    @Operation(summary = "发送验证码")
    @PostMapping("/sendCode")
    public CommonResponse<Void> sendVerificationCode(@RequestBody SendCodeRequest request) {
        userLoginService.sendVerificationCode(request.getPhone());
        return CommonResponse.buildSuccess();
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public CommonResponse<RegisterResponse> register(@RequestBody RegisterRequest request) {
        String token = userLoginService.register(
                request.getPhone(),
                request.getVerificationCode(),
                request.getPassword()
        );

        RegisterResponse response = new RegisterResponse();
        response.setToken(token);
        return CommonResponse.buildSuccess(response);
    }

    @Data
    @Schema(description = "发送验证码请求")
    public static class SendCodeRequest {
        @Schema(description = "手机号", example = "13800138001")
        private String phone;
    }

    @Data
    @Schema(description = "注册请求")
    public static class RegisterRequest {
        @Schema(description = "手机号", example = "13800138001")
        private String phone;

        @Schema(description = "验证码", example = "1234")
        private String verificationCode;

        @Schema(description = "密码（至少8位）", example = "12345678")
        private String password;
    }

    @Data
    @Schema(description = "注册响应")
    public static class RegisterResponse {
        @Schema(description = "登录令牌")
        private String token;
    }

    @Operation(summary = "密码登录")
    @PostMapping("/login")
    public CommonResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = userLoginService.loginByPassword(
                request.getPhone(),
                request.getPassword()
        );

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return CommonResponse.buildSuccess(response);
    }

    @Operation(summary = "验证码登录")
    @PostMapping("/loginByCode")
    public CommonResponse<LoginResponse> loginByCode(@RequestBody LoginByCodeRequest request) {
        String token = userLoginService.loginByCode(
                request.getPhone(),
                request.getVerificationCode()
        );

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return CommonResponse.buildSuccess(response);
    }

    @Data
    @Schema(description = "密码登录请求")
    public static class LoginRequest {
        @Schema(description = "手机号", example = "13800138001")
        private String phone;

        @Schema(description = "密码", example = "12345678")
        private String password;
    }

    @Data
    @Schema(description = "验证码登录请求")
    public static class LoginByCodeRequest {
        @Schema(description = "手机号", example = "13800138001")
        private String phone;

        @Schema(description = "验证码", example = "1234")
        private String verificationCode;
    }

    @Data
    @Schema(description = "登录响应")
    public static class LoginResponse {
        @Schema(description = "登录令牌")
        private String token;
    }
}