package com.life.bank.palm.dao.community.mapper;

import com.life.bank.palm.dao.community.pojo.CommentPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {

    int insertSelective(CommentPO comment);

    CommentPO selectById(@Param("id") Integer id);

    List<CommentPO> selectByPostId(@Param("postId") Integer postId);

    List<CommentPO> selectByParentId(@Param("parentId") Integer parentId);

    int updateLikeCount(@Param("id") Integer id,
                        @Param("likeCount") Integer likeCount);

    int deleteById(@Param("id") Integer id);
}