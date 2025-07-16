package com.life.bank.palm.dao.trade.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class TradeRecordPO {
    private Integer id;
    private Integer userId;
    private String tradeId;
    private Integer tradeType;
    private String tradeAmount;
    private String tradeBalance;
    private Integer tradeChannel;
    private Date tradeTime;
    private Integer tradeStatus;
    private String tradeDesc;
    private Integer targetUserId;
    private String targetCardId;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
}