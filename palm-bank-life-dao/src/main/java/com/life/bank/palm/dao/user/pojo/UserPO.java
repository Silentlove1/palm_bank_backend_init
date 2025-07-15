package com.life.bank.palm.dao.user.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class UserPO {
    private Integer id;
    private String nickname;
    private String schoolName;
    private Integer gender;
    private String phone;
    private String email;
    private String logo;
    private String password;
    private String cardId;
    private String balance;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
}