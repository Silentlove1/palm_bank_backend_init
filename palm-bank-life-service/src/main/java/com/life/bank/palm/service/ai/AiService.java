package com.life.bank.palm.service.ai;

import com.alibaba.fastjson.JSON;
import com.life.bank.palm.common.config.AiConfig;
import com.life.bank.palm.common.utils.SseEmitterUtil;
import com.life.bank.palm.service.ai.dto.AiMessage;
import com.life.bank.palm.service.ai.dto.AiRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class AiService {

    @Autowired
    private AiConfig aiConfig;

    private static final String SYSTEM_PROMPT =
            "你是掌上银行的智能客服助手。你需要帮助用户解答关于银行业务的问题，" +
                    "包括转账、充值、对账、理财等功能。请保持专业、友好的态度，" +
                    "并根据用户的问题提供准确、有用的信息。";

    /**
     * 创建AI对话会话
     */
//    public String createSession(String question) {
//        String sessionId = UUID.randomUUID().toString();
//
//        // 异步处理AI响应
//        CompletableFuture.runAsync(() -> {
//            try {
//                // 延迟模拟网络请求
//                Thread.sleep(500);
//
//                // 模拟AI响应（实际项目中应该调用真实的AI API）
//                String response = generateMockResponse(question);
//
//                // 分段发送响应，模拟流式输出
//                String[] words = response.split("");
//                for (int i = 0; i < words.length; i++) {
//                    SseEmitterUtil.send(sessionId, JSON.toJSONString(new StreamData(words[i], false)));
//                    Thread.sleep(20); // 模拟打字效果
//                }
//
//                // 发送结束标记
//                SseEmitterUtil.send(sessionId, JSON.toJSONString(new StreamData("", true)));
//                SseEmitterUtil.close(sessionId);
//
//            } catch (Exception e) {
//                log.error("AI响应处理失败", e);
//                SseEmitterUtil.send(sessionId, JSON.toJSONString(
//                        new StreamData("抱歉，系统出现了一些问题，请稍后再试。", true)));
//                SseEmitterUtil.close(sessionId);
//            }
//        });
//
//        return sessionId;
//    }

    public String createSession(String question) {
        String sessionId = UUID.randomUUID().toString();

        // 异步处理AI响应
        CompletableFuture.runAsync(() -> {
            try {
                // 立即发送一个初始消息，避免超时
                SseEmitterUtil.send(sessionId, JSON.toJSONString(new StreamData("", false)));

                // 延迟模拟网络请求
                Thread.sleep(500);

                // 模拟AI响应
                String response = generateMockResponse(question);

                // 分段发送响应
                String[] words = response.split("");
                for (int i = 0; i < words.length; i++) {
                    SseEmitterUtil.send(sessionId, JSON.toJSONString(new StreamData(words[i], false)));
                    Thread.sleep(20); // 模拟打字效果
                }

                // 发送结束标记
                SseEmitterUtil.send(sessionId, JSON.toJSONString(new StreamData("", true)));
                SseEmitterUtil.close(sessionId);

            } catch (Exception e) {
                log.error("AI响应处理失败", e);
                try {
                    SseEmitterUtil.send(sessionId, JSON.toJSONString(
                            new StreamData("抱歉，系统出现了一些问题，请稍后再试。", true)));
                } catch (Exception ex) {
                    log.error("发送错误消息失败", ex);
                }
                SseEmitterUtil.close(sessionId);
            }
        });

        return sessionId;
    }

    /**
     * 模拟生成AI响应
     */
    private String generateMockResponse(String question) {
        // 根据关键词返回不同的回答
        if (question.contains("转账")) {
            return "关于转账功能：\n\n" +
                    "1. 转账限额：单笔最高5万元\n" +
                    "2. 转账步骤：进入转账页面 → 输入对方卡号 → 输入金额 → 确认转账\n" +
                    "3. 转账手续费：同行转账免费，跨行转账根据金额收取少量手续费\n" +
                    "4. 到账时间：同行实时到账，跨行2小时内到账\n\n" +
                    "请问还有什么其他问题吗？";

        } else if (question.contains("充值")) {
            return "充值功能说明：\n\n" +
                    "1. 支持充值渠道：支付宝、微信、云闪付\n" +
                    "2. 充值限额：单笔最高5万元，每日累计最高20万元\n" +
                    "3. 充值步骤：选择充值渠道 → 输入充值金额 → 跳转支付 → 完成充值\n" +
                    "4. 到账时间：一般实时到账，最迟不超过2小时\n\n" +
                    "温馨提示：充值时请确保支付渠道余额充足。";

        } else if (question.contains("对账") || question.contains("统计")) {
            return "对账统计功能介绍：\n\n" +
                    "1. 日统计：查看每日的收入和支出明细\n" +
                    "2. 月统计：按月汇总收支情况，便于理财规划\n" +
                    "3. 总览：查看账户总收入、总支出等关键数据\n" +
                    "4. 导出功能：支持导出Excel报表（功能开发中）\n\n" +
                    "通过对账功能，您可以清晰了解资金流向，更好地管理财务。";

        } else if (question.contains("理财")) {
            return "理财服务介绍：\n\n" +
                    "我们提供多种理财产品：\n" +
                    "1. 活期理财：随存随取，年化收益2.5%左右\n" +
                    "2. 定期理财：期限灵活，年化收益3%-5%\n" +
                    "3. 基金产品：多种类型可选，适合不同风险偏好\n\n" +
                    "建议您根据自身情况选择合适的理财产品。如需详细了解，可以预约理财经理。";

        } else if (question.contains("社区")) {
            return "社区功能说明：\n\n" +
                    "1. 发帖分享：分享您的理财心得和使用体验\n" +
                    "2. 互动交流：点赞、评论、收藏感兴趣的内容\n" +
                    "3. 学习交流：向其他用户学习理财经验\n" +
                    "4. 官方资讯：获取最新的银行公告和活动信息\n\n" +
                    "欢迎您积极参与社区交流！";

        } else {
            return "您好！我是掌上银行的智能客服助手。\n\n" +
                    "我可以帮您解答以下问题：\n" +
                    "• 转账相关问题\n" +
                    "• 充值操作指引\n" +
                    "• 对账统计查询\n" +
                    "• 理财产品咨询\n" +
                    "• 社区功能使用\n\n" +
                    "请问有什么可以帮助您的吗？";
        }
    }

    /**
     * 调用真实AI API（预留接口）
     */
    public void callRealAiApi(String sessionId, String question) {
        // TODO: 实现真实的AI API调用
        // 1. 构建请求
        // 2. 发送HTTP请求到AI服务
        // 3. 处理流式响应
        // 4. 通过SSE推送给前端
    }

    @Data
    public static class StreamData {
        private String content;
        private boolean finished;

        public StreamData(String content, boolean finished) {
            this.content = content;
            this.finished = finished;
        }
    }
}