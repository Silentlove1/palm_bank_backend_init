package com.life.bank.palm.dao.trade.mapper;

import com.life.bank.palm.dao.trade.pojo.TradeBoardPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TradeBoardMapper {

    TradeBoardPO selectByUserIdAndDateTypeAndDateStr(@Param("userId") Integer userId,
                                                     @Param("dateType") Integer dateType,
                                                     @Param("dateStr") String dateStr);

    int insertSelective(TradeBoardPO record);

    int updateAmounts(@Param("id") Integer id,
                      @Param("totalIncome") String totalIncome,
                      @Param("totalExpense") String totalExpense,
                      @Param("rechargeAmount") String rechargeAmount,
                      @Param("withdrawAmount") String withdrawAmount,
                      @Param("transferInAmount") String transferInAmount,
                      @Param("transferOutAmount") String transferOutAmount);

    List<TradeBoardPO> selectByUserIdAndDateType(@Param("userId") Integer userId,
                                                 @Param("dateType") Integer dateType,
                                                 @Param("limit") Integer limit);
}