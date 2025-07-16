package com.life.bank.palm.dao.trade.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class TradeTokenPO {
    private Integer id;
    private Integer userId;
    private String token;
    private String tradeType;
    private Date expireTime;
    private Date createTime;
    private Integer isDelete;
}