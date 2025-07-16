package com.life.bank.palm.service.trade;

import com.life.bank.palm.common.context.UserContext;
import com.life.bank.palm.common.exception.CommonBizException;
import com.life.bank.palm.common.utils.CheckUtil;
import com.life.bank.palm.common.utils.SnowflakeIdGenerator;
import com.life.bank.palm.dao.trade.mapper.TradeRecordMapper;
import com.life.bank.palm.dao.trade.pojo.TradeRecordPO;
import com.life.bank.palm.dao.user.mapper.UserMapper;
import com.life.bank.palm.dao.user.pojo.UserPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TradeService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TradeRecordMapper tradeRecordMapper;

    @Autowired
    private RedissonClient redissonClient;

    private static final String TRANSFER_LOCK_KEY = "palm.bank.transfer.lock.%s";

    /**
     * 转账
     */
    @Transactional(rollbackFor = Exception.class)
    public String transfer(String targetCardId, String amount, String desc) {
        // 参数校验
        CheckUtil.Biz.INSTANCE
                .strNotBlank(targetCardId, "收款卡号不能为空")
                .strNotBlank(amount, "转账金额不能为空")
                .isTrue(amount.matches("\\d+(\\.\\d{1,2})?"), "金额格式不正确");

        BigDecimal transferAmount = new BigDecimal(amount);
        CheckUtil.Biz.INSTANCE
                .isTrue(transferAmount.compareTo(BigDecimal.ZERO) > 0, "转账金额必须大于0")
                .isTrue(transferAmount.compareTo(new BigDecimal("50000")) <= 0, "单笔转账限额5万元");

        // 获取当前用户
        Integer userId = UserContext.getUserId();
        UserPO fromUser = userMapper.selectOneByIdAndIsDelete(userId, 0);

        // 查询收款用户
        UserPO toUser = userMapper.selectOneByCardIdAndIsDelete(targetCardId, 0);
        CheckUtil.Biz.INSTANCE
                .noNull(toUser, "收款账户不存在")
                .isTrue(!fromUser.getId().equals(toUser.getId()), "不能给自己转账");

        // 获取分布式锁，防止并发
        String lockKey = String.format(TRANSFER_LOCK_KEY, userId);
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试加锁，最多等待3秒，锁定10秒后自动释放
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                throw new CommonBizException("系统繁忙，请稍后再试");
            }

            // 再次查询最新余额（防止并发）
            fromUser = userMapper.selectOneByIdAndIsDelete(userId, 0);
            BigDecimal fromBalance = new BigDecimal(fromUser.getBalance());

            // 检查余额
            if (fromBalance.compareTo(transferAmount) < 0) {
                throw new CommonBizException("余额不足");
            }

            // 计算新余额
            BigDecimal newFromBalance = fromBalance.subtract(transferAmount);
            BigDecimal toBalance = new BigDecimal(toUser.getBalance());
            BigDecimal newToBalance = toBalance.add(transferAmount);

            // 生成交易ID
            String tradeId = String.valueOf(SnowflakeIdGenerator.getInstance().nextId());
            Date now = new Date();

            // 更新转出方余额
            userMapper.updateBalanceById(newFromBalance.toString(), fromUser.getId());

            // 记录转出交易
            TradeRecordPO fromRecord = new TradeRecordPO();
            fromRecord.setUserId(fromUser.getId());
            fromRecord.setTradeId(tradeId + "_out");
            fromRecord.setTradeType(4); // 转账支出
            fromRecord.setTradeAmount(transferAmount.toString());
            fromRecord.setTradeBalance(newFromBalance.toString());
            fromRecord.setTradeChannel(4); // 银行转账
            fromRecord.setTradeTime(now);
            fromRecord.setTradeStatus(2); // 成功
            fromRecord.setTradeDesc(StringUtils.isBlank(desc) ? "转账支出" : desc);
            fromRecord.setTargetUserId(toUser.getId());
            fromRecord.setTargetCardId(targetCardId);
            tradeRecordMapper.insertSelective(fromRecord);

            // 更新转入方余额
            userMapper.updateBalanceById(newToBalance.toString(), toUser.getId());

            // 记录转入交易
            TradeRecordPO toRecord = new TradeRecordPO();
            toRecord.setUserId(toUser.getId());
            toRecord.setTradeId(tradeId + "_in");
            toRecord.setTradeType(3); // 转账收入
            toRecord.setTradeAmount(transferAmount.toString());
            toRecord.setTradeBalance(newToBalance.toString());
            toRecord.setTradeChannel(4); // 银行转账
            toRecord.setTradeTime(now);
            toRecord.setTradeStatus(2); // 成功
            toRecord.setTradeDesc("来自" + fromUser.getNickname() + "的转账");
            toRecord.setTargetUserId(fromUser.getId());
            toRecord.setTargetCardId(fromUser.getCardId());
            tradeRecordMapper.insertSelective(toRecord);

            log.info("转账成功：{} -> {}, 金额：{}", fromUser.getCardId(), targetCardId, amount);

            return tradeId;

        } catch (InterruptedException e) {
            throw new CommonBizException("系统异常，请稍后再试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 充值（模拟）
     */
    @Transactional(rollbackFor = Exception.class)
    public String recharge(String amount, Integer channel) {
        // 参数校验
        CheckUtil.Biz.INSTANCE
                .strNotBlank(amount, "充值金额不能为空")
                .isTrue(amount.matches("\\d+(\\.\\d{1,2})?"), "金额格式不正确");

        BigDecimal rechargeAmount = new BigDecimal(amount);
        CheckUtil.Biz.INSTANCE
                .isTrue(rechargeAmount.compareTo(BigDecimal.ZERO) > 0, "充值金额必须大于0")
                .isTrue(rechargeAmount.compareTo(new BigDecimal("50000")) <= 0, "单笔充值限额5万元");

        // 获取当前用户
        Integer userId = UserContext.getUserId();
        UserPO user = userMapper.selectOneByIdAndIsDelete(userId, 0);

        // 获取分布式锁
        String lockKey = String.format(TRANSFER_LOCK_KEY, userId);
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                throw new CommonBizException("系统繁忙，请稍后再试");
            }

            // 再次查询最新余额
            user = userMapper.selectOneByIdAndIsDelete(userId, 0);
            BigDecimal balance = new BigDecimal(user.getBalance());
            BigDecimal newBalance = balance.add(rechargeAmount);

            // 生成交易ID
            String tradeId = String.valueOf(SnowflakeIdGenerator.getInstance().nextId());
            Date now = new Date();

            // 更新余额
            userMapper.updateBalanceById(newBalance.toString(), user.getId());

            // 记录交易
            TradeRecordPO record = new TradeRecordPO();
            record.setUserId(user.getId());
            record.setTradeId(tradeId);
            record.setTradeType(1); // 充值
            record.setTradeAmount(rechargeAmount.toString());
            record.setTradeBalance(newBalance.toString());
            record.setTradeChannel(channel);
            record.setTradeTime(now);
            record.setTradeStatus(2); // 成功
            record.setTradeDesc(getChannelName(channel) + "充值");
            tradeRecordMapper.insertSelective(record);

            log.info("充值成功：用户{}，金额：{}，渠道：{}", userId, amount, channel);

            return tradeId;

        } catch (InterruptedException e) {
            throw new CommonBizException("系统异常，请稍后再试");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private String getChannelName(Integer channel) {
        switch (channel) {
            case 1: return "支付宝";
            case 2: return "微信";
            case 3: return "云闪付";
            case 4: return "银行转账";
            default: return "其他";
        }
    }
}