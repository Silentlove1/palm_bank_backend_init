package com.life.bank.palm.dao.user.mapper;

import com.life.bank.palm.dao.user.pojo.UserTokenPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserTokenMapper {

    int insertSelective(UserTokenPO userTokenPO);

    int updateIsDeleteByUserIdAndPlatform(@Param("isDelete") Integer isDelete,
                                          @Param("userId") Integer userId,
                                          @Param("platform") Integer platform);

    UserTokenPO selectOneByTokenAndIsDelete(@Param("token") String token,
                                            @Param("isDelete") Integer isDelete);
}