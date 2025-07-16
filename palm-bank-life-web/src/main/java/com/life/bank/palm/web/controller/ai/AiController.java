package com.life.bank.palm.web.controller.ai;

import com.life.bank.palm.common.result.CommonResponse;
import com.life.bank.palm.common.utils.SseEmitterUtil;
import com.life.bank.palm.service.ai.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Tag(name = "AI智能客服")
@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @Operation(summary = "创建AI对话")
    @PostMapping("/chat")
    public CommonResponse<ChatResponse> createChat(@RequestBody ChatRequest request) {
        String sessionId = aiService.createSession(request.getQuestion());

        ChatResponse response = new ChatResponse();
        response.setSessionId(sessionId);
        return CommonResponse.buildSuccess(response);
    }

//    @Operation(summary = "SSE连接 - 接收AI响应流")
//    @GetMapping(value = "/sse/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter connectSse(@PathVariable String sessionId) {
//        log.info("建立SSE连接：{}", sessionId);
//        // 设置30秒超时
//        return SseEmitterUtil.create(sessionId, 30000L);
//    }

    @Operation(summary = "SSE连接 - 接收AI响应流")
    @GetMapping(value = "/sse/{sessionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectSse(@PathVariable String sessionId) {
        log.info("建立SSE连接：{}", sessionId);
        // 设置60秒超时（原来是30秒）
        return SseEmitterUtil.create(sessionId, 60000L);
    }

    @Operation(summary = "获取常见问题")
    @GetMapping("/faq")
    public CommonResponse<FaqResponse> getFaq() {
        FaqResponse response = new FaqResponse();
        response.getQuestions().add("如何进行转账操作？");
        response.getQuestions().add("充值支持哪些渠道？");
        response.getQuestions().add("如何查看我的交易记录？");
        response.getQuestions().add("理财产品有哪些？");
        response.getQuestions().add("如何使用对账功能？");
        response.getQuestions().add("社区功能怎么使用？");
        response.getQuestions().add("转账限额是多少？");
        response.getQuestions().add("如何保证账户安全？");

        return CommonResponse.buildSuccess(response);
    }

    @Data
    @Schema(description = "创建对话请求")
    public static class ChatRequest {
        @Schema(description = "用户问题", example = "如何进行转账？")
        private String question;
    }

    @Data
    @Schema(description = "创建对话响应")
    public static class ChatResponse {
        @Schema(description = "会话ID")
        private String sessionId;
    }

    @Data
    @Schema(description = "常见问题响应")
    public static class FaqResponse {
        @Schema(description = "常见问题列表")
        private java.util.List<String> questions = new java.util.ArrayList<>();
    }

}