package com.life.bank.palm.web.controller.board;

import com.life.bank.palm.common.context.UserContext;
import com.life.bank.palm.common.result.CommonResponse;
import com.life.bank.palm.dao.trade.mapper.TradeBoardMapper;
import com.life.bank.palm.dao.trade.pojo.TradeBoardPO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "对账统计")
@RestController
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private TradeBoardMapper tradeBoardMapper;

    @Operation(summary = "获取统计概览")
    @GetMapping("/overview")
    public CommonResponse<BoardOverviewResponse> getOverview() {
        Integer userId = UserContext.getUserId();

        // 获取总统计
        TradeBoardPO totalBoard = tradeBoardMapper.selectByUserIdAndDateTypeAndDateStr(
                userId, 3, "total");

        BoardOverviewResponse response = new BoardOverviewResponse();
        if (totalBoard != null) {
            response.setTotalIncome(totalBoard.getTotalIncome());
            response.setTotalExpense(totalBoard.getTotalExpense());
            response.setRechargeAmount(totalBoard.getRechargeAmount());
            response.setWithdrawAmount(totalBoard.getWithdrawAmount());
            response.setTransferInAmount(totalBoard.getTransferInAmount());
            response.setTransferOutAmount(totalBoard.getTransferOutAmount());
        } else {
            response.setTotalIncome("0");
            response.setTotalExpense("0");
            response.setRechargeAmount("0");
            response.setWithdrawAmount("0");
            response.setTransferInAmount("0");
            response.setTransferOutAmount("0");
        }

        return CommonResponse.buildSuccess(response);
    }

    @Operation(summary = "获取日统计列表")
    @GetMapping("/daily")
    public CommonResponse<List<BoardItemResponse>> getDailyList(
            @RequestParam(defaultValue = "7") Integer days) {

        Integer userId = UserContext.getUserId();
        List<TradeBoardPO> boards = tradeBoardMapper.selectByUserIdAndDateType(userId, 1, days);

        List<BoardItemResponse> responseList = new ArrayList<>();
        for (TradeBoardPO board : boards) {
            BoardItemResponse item = new BoardItemResponse();
            item.setDateStr(board.getDateStr());
            item.setTotalIncome(board.getTotalIncome());
            item.setTotalExpense(board.getTotalExpense());
            item.setRechargeAmount(board.getRechargeAmount());
            item.setWithdrawAmount(board.getWithdrawAmount());
            item.setTransferInAmount(board.getTransferInAmount());
            item.setTransferOutAmount(board.getTransferOutAmount());
            responseList.add(item);
        }

        return CommonResponse.buildSuccess(responseList);
    }

    @Operation(summary = "获取月统计列表")
    @GetMapping("/monthly")
    public CommonResponse<List<BoardItemResponse>> getMonthlyList(
            @RequestParam(defaultValue = "12") Integer months) {

        Integer userId = UserContext.getUserId();
        List<TradeBoardPO> boards = tradeBoardMapper.selectByUserIdAndDateType(userId, 2, months);

        List<BoardItemResponse> responseList = new ArrayList<>();
        for (TradeBoardPO board : boards) {
            BoardItemResponse item = new BoardItemResponse();
            item.setDateStr(board.getDateStr());
            item.setTotalIncome(board.getTotalIncome());
            item.setTotalExpense(board.getTotalExpense());
            item.setRechargeAmount(board.getRechargeAmount());
            item.setWithdrawAmount(board.getWithdrawAmount());
            item.setTransferInAmount(board.getTransferInAmount());
            item.setTransferOutAmount(board.getTransferOutAmount());
            responseList.add(item);
        }

        return CommonResponse.buildSuccess(responseList);
    }

    @Data
    @Schema(description = "统计概览响应")
    public static class BoardOverviewResponse {
        @Schema(description = "总收入")
        private String totalIncome;

        @Schema(description = "总支出")
        private String totalExpense;

        @Schema(description = "充值总额")
        private String rechargeAmount;

        @Schema(description = "提现总额")
        private String withdrawAmount;

        @Schema(description = "转入总额")
        private String transferInAmount;

        @Schema(description = "转出总额")
        private String transferOutAmount;
    }

    @Data
    @Schema(description = "统计项响应")
    public static class BoardItemResponse {
        @Schema(description = "日期")
        private String dateStr;

        @Schema(description = "总收入")
        private String totalIncome;

        @Schema(description = "总支出")
        private String totalExpense;

        @Schema(description = "充值金额")
        private String rechargeAmount;

        @Schema(description = "提现金额")
        private String withdrawAmount;

        @Schema(description = "转入金额")
        private String transferInAmount;

        @Schema(description = "转出金额")
        private String transferOutAmount;
    }
}