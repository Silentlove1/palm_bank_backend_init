package com.life.bank.palm.dao.community.mapper;

import com.life.bank.palm.dao.community.pojo.UserCollectPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserCollectMapper {

    int insertSelective(UserCollectPO userCollect);

    UserCollectPO selectByUserIdAndPostId(@Param("userId") Integer userId,
                                          @Param("postId") Integer postId);

    List<UserCollectPO> selectByUserId(@Param("userId") Integer userId,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

    int deleteById(@Param("id") Integer id);

    List<Integer> selectPostIdsByUserId(@Param("userId") Integer userId);
}