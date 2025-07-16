package com.life.bank.palm.dao.user.mapper;

import com.life.bank.palm.dao.user.pojo.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper  // 添加这个注解
public interface UserMapper {

    UserPO selectOneByPhoneAndIsDelete(@Param("phone") String phone,
                                       @Param("isDelete") Integer isDelete);

    int insertSelective(UserPO userPO);

    UserPO selectOneByIdAndIsDelete(@Param("id") Integer id,
                                    @Param("isDelete") Integer isDelete);

    int updateBalanceById(@Param("balance") String balance,
                          @Param("id") Integer id);

    UserPO selectOneByCardIdAndIsDelete(@Param("cardId") String cardId,
                                        @Param("isDelete") Integer isDelete);
}