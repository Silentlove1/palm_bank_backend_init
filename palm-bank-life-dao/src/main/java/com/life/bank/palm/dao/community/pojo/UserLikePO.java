package com.life.bank.palm.dao.community.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class UserLikePO {
    private Integer id;
    private Integer userId;
    private Integer targetId;
    private Integer targetType;
    private Date createTime;
    private Integer isDelete;
}