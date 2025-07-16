package com.life.bank.palm.dao.community.mapper;

import com.life.bank.palm.dao.community.pojo.PostPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PostMapper {

    int insertSelective(PostPO post);

    PostPO selectById(@Param("id") Integer id);

    List<PostPO> selectList(@Param("offset") Integer offset,
                            @Param("limit") Integer limit);

    int updateViewCount(@Param("id") Integer id);

    int updateCounts(@Param("id") Integer id,
                     @Param("likeCount") Integer likeCount,
                     @Param("commentCount") Integer commentCount,
                     @Param("collectCount") Integer collectCount);

    List<PostPO> selectByUserId(@Param("userId") Integer userId,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);
}