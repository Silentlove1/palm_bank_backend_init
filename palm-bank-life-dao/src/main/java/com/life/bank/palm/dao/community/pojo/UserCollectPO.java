package com.life.bank.palm.dao.community.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class UserCollectPO {
    private Integer id;
    private Integer userId;
    private Integer postId;
    private Date createTime;
    private Integer isDelete;
}