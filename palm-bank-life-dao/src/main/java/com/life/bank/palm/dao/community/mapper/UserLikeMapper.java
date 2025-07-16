package com.life.bank.palm.dao.community.mapper;

import com.life.bank.palm.dao.community.pojo.UserLikePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserLikeMapper {

    int insertSelective(UserLikePO userLike);

    UserLikePO selectByUserIdAndTarget(@Param("userId") Integer userId,
                                       @Param("targetId") Integer targetId,
                                       @Param("targetType") Integer targetType);

    List<UserLikePO> selectByUserId(@Param("userId") Integer userId,
                                    @Param("targetType") Integer targetType);

    int deleteById(@Param("id") Integer id);

    List<Integer> selectTargetIdsByUserIdAndType(@Param("userId") Integer userId,
                                                 @Param("targetType") Integer targetType);
}