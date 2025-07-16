package com.life.bank.palm.dao.trade.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class TradeBoardPO {
    private Integer id;
    private Integer userId;
    private Integer dateType;
    private String dateStr;
    private String totalIncome;
    private String totalExpense;
    private String rechargeAmount;
    private String withdrawAmount;
    private String transferInAmount;
    private String transferOutAmount;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
}