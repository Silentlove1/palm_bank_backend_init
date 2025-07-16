package com.life.bank.palm.web.controller.trade;

import com.life.bank.palm.common.context.UserContext;
import com.life.bank.palm.common.result.CommonResponse;
import com.life.bank.palm.dao.trade.mapper.TradeRecordMapper;
import com.life.bank.palm.dao.trade.pojo.TradeRecordPO;
import com.life.bank.palm.dao.user.mapper.UserMapper;
import com.life.bank.palm.dao.user.pojo.UserPO;
import com.life.bank.palm.service.trade.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "交易管理")
@RestController
@RequestMapping("/trade")
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TradeRecordMapper tradeRecordMapper;

    @Operation(summary = "转账")
    @PostMapping("/transfer")
    public CommonResponse<TransferResponse> transfer(@RequestBody TransferRequest request) {
        String tradeId = tradeService.transfer(
                request.getTargetCardId(),
                request.getAmount(),
                request.getDesc()
        );

        TransferResponse response = new TransferResponse();
        response.setTradeId(tradeId);
        response.setSuccess(true);
        return CommonResponse.buildSuccess(response);
    }

    @Operation(summary = "查询余额")
    @GetMapping("/balance")
    public CommonResponse<BalanceResponse> getBalance() {
        Integer userId = UserContext.getUserId();
        UserPO user = userMapper.selectOneByIdAndIsDelete(userId, 0);

        BalanceResponse response = new BalanceResponse();
        response.setBalance(user.getBalance());
        response.setCardId(user.getCardId());
        response.setNickname(user.getNickname());
        return CommonResponse.buildSuccess(response);
    }

    @Operation(summary = "交易记录查询")
    @GetMapping("/records")
    public CommonResponse<List<TradeRecordResponse>> getTradeRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        Integer userId = UserContext.getUserId();
        Integer offset = (page - 1) * pageSize;

        List<TradeRecordPO> records = tradeRecordMapper.selectByUserId(userId, offset, pageSize);

        List<TradeRecordResponse> responseList = new ArrayList<>();
        for (TradeRecordPO record : records) {
            TradeRecordResponse response = new TradeRecordResponse();
            response.setTradeId(record.getTradeId());
            response.setTradeType(record.getTradeType());
            response.setTradeTypeName(getTradeTypeName(record.getTradeType()));
            response.setTradeAmount(record.getTradeAmount());
            response.setTradeBalance(record.getTradeBalance());
            response.setTradeTime(record.getTradeTime());
            response.setTradeStatus(record.getTradeStatus());
            response.setTradeDesc(record.getTradeDesc());
            response.setTargetCardId(record.getTargetCardId());
            responseList.add(response);
        }

        return CommonResponse.buildSuccess(responseList);
    }

    private String getTradeTypeName(Integer tradeType) {
        switch (tradeType) {
            case 1: return "充值";
            case 2: return "提现";
            case 3: return "转账收入";
            case 4: return "转账支出";
            default: return "未知";
        }
    }

    @Data
    @Schema(description = "转账请求")
    public static class TransferRequest {
        @Schema(description = "收款卡号", example = "6228123456789012")
        private String targetCardId;

        @Schema(description = "转账金额", example = "100.00")
        private String amount;

        @Schema(description = "转账备注", example = "还款")
        private String desc;
    }

    @Data
    @Schema(description = "转账响应")
    public static class TransferResponse {
        @Schema(description = "交易ID")
        private String tradeId;

        @Schema(description = "是否成功")
        private Boolean success;
    }

    @Data
    @Schema(description = "余额响应")
    public static class BalanceResponse {
        @Schema(description = "余额")
        private String balance;

        @Schema(description = "银行卡号")
        private String cardId;

        @Schema(description = "用户昵称")
        private String nickname;
    }

    @Data
    @Schema(description = "交易记录响应")
    public static class TradeRecordResponse {
        @Schema(description = "交易ID")
        private String tradeId;

        @Schema(description = "交易类型")
        private Integer tradeType;

        @Schema(description = "交易类型名称")
        private String tradeTypeName;

        @Schema(description = "交易金额")
        private String tradeAmount;

        @Schema(description = "交易后余额")
        private String tradeBalance;

        @Schema(description = "交易时间")
        private java.util.Date tradeTime;

        @Schema(description = "交易状态：1-处理中，2-成功，3-失败")
        private Integer tradeStatus;

        @Schema(description = "交易描述")
        private String tradeDesc;

        @Schema(description = "对方卡号")
        private String targetCardId;
    }

    @Operation(summary = "充值（模拟）")
    @PostMapping("/recharge")
    public CommonResponse<RechargeResponse> recharge(@RequestBody RechargeRequest request) {
        String tradeId = tradeService.recharge(
                request.getAmount(),
                request.getChannel()
        );

        RechargeResponse response = new RechargeResponse();
        response.setTradeId(tradeId);
        response.setSuccess(true);
        return CommonResponse.buildSuccess(response);
    }

    @Data
    @Schema(description = "充值请求")
    public static class RechargeRequest {
        @Schema(description = "充值金额", example = "1000.00")
        private String amount;

        @Schema(description = "充值渠道：1-支付宝，2-微信，3-云闪付", example = "1")
        private Integer channel;
    }

    @Data
    @Schema(description = "充值响应")
    public static class RechargeResponse {
        @Schema(description = "交易ID")
        private String tradeId;

        @Schema(description = "是否成功")
        private Boolean success;
    }
}