package com.life.bank.palm.dao.community.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class CommentPO {
    private Integer id;
    private Integer postId;
    private Integer userId;
    private Integer parentId;
    private String content;
    private Integer likeCount;
    private Date createTime;
    private Integer isDelete;
}