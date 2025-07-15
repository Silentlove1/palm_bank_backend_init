package com.life.bank.palm.dao.user.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class UserTokenPO {
    private Integer id;
    private Integer userId;
    private String token;
    private Integer platform;
    private Date expireTime;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
}