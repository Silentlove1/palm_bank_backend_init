package com.life.bank.palm.dao.community.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class PostPO {
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDelete;
}