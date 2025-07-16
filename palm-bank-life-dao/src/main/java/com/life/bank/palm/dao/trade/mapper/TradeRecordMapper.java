package com.life.bank.palm.dao.trade.mapper;

import com.life.bank.palm.dao.trade.pojo.TradeRecordPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TradeRecordMapper {

    int insertSelective(TradeRecordPO record);

    TradeRecordPO selectByTradeId(@Param("tradeId") String tradeId);

    List<TradeRecordPO> selectByUserId(@Param("userId") Integer userId,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);

    int updateStatusByTradeId(@Param("tradeStatus") Integer tradeStatus,
                              @Param("tradeId") String tradeId);
}