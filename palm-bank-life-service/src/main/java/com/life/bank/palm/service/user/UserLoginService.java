package com.life.bank.palm.service.user;

import com.life.bank.palm.common.utils.CheckUtil;
import com.life.bank.palm.dao.user.mapper.UserMapper;
import com.life.bank.palm.dao.user.mapper.UserTokenMapper;
import com.life.bank.palm.dao.user.pojo.UserPO;
import com.life.bank.palm.dao.user.pojo.UserTokenPO;
import com.life.bank.palm.service.user.utils.AESEncryptUtil;
import com.life.bank.palm.service.user.utils.PhoneCheckUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserLoginService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Autowired
    private RedissonClient redissonClient;

    // 验证码缓存key
    private static final String VERIFICATION_CODE_KEY = "palm.bank.verification.code.%s";

    // 验证码过期时间（分钟）
    private static final Integer VERIFICATION_CODE_EXPIRE_TIME = 3;

    /**
     * 发送验证码
     */
    public void sendVerificationCode(String phone) {
        CheckUtil.Biz.INSTANCE
                .isTrue(PhoneCheckUtil.checkPhoneValidate(phone), "输入手机号不合法");

        RBucket<String> codeCache = redissonClient.getBucket(String.format(VERIFICATION_CODE_KEY, phone));

        // 生成4位数字验证码
        String code = RandomStringUtils.randomNumeric(4);

        // 存入Redis，3分钟过期
        codeCache.set(code, VERIFICATION_CODE_EXPIRE_TIME, TimeUnit.MINUTES);

        // TODO: 实际项目中这里应该调用短信服务发送验证码
        System.out.println("【模拟发送短信】手机号：" + phone + "，验证码：" + code);
    }

    /**
     * 用户注册
     */
    public String register(String phone, String verificationCode, String password) {
        // 验证输入合法性
        CheckUtil.Biz.INSTANCE
                .strNotBlank(verificationCode, "验证码不能为空")
                .isTrue(verificationCode.length() == 4, "验证码长度不符")
                .strNotBlank(password, "密码不能为空")
                .isTrue(password.length() >= 8, "密码不能低于8位数");

        // 检查手机号是否已注册
        UserPO existUser = userMapper.selectOneByPhoneAndIsDelete(phone, NumberUtils.INTEGER_ZERO);
        CheckUtil.Biz.INSTANCE
                .isTrue(existUser == null, "当前手机号已注册");

        // 验证验证码
        RBucket<String> codeCache = redissonClient.getBucket(String.format(VERIFICATION_CODE_KEY, phone));
        String code = codeCache.get();
        CheckUtil.Biz.INSTANCE
                .strNotBlank(code, "验证码已过期")
                .isTrue(verificationCode.equals(code), "验证码不正确");

        // 创建新用户
        UserPO user = new UserPO();
        user.setPhone(phone);
        user.setPassword(AESEncryptUtil.encrypt(password));
        user.setNickname("用户" + RandomStringUtils.randomAlphanumeric(8));
        user.setCardId(generateCardId());
        user.setBalance("0");
        user.setLogo("https://img.icons8.com/color/96/000000/user.png");

        // 保存用户
        userMapper.insertSelective(user);

        // 删除验证码
        codeCache.delete();

        // 生成token并返回
        return generateToken(user);
    }

    /**
     * 生成银行卡号（16位）
     */
    private String generateCardId() {
        // 实际项目中应该确保唯一性
        return "6228" + RandomStringUtils.randomNumeric(12);
    }

    /**
     * 生成登录token
     */
    private String generateToken(UserPO user) {
        // 清除旧token
        userTokenMapper.updateIsDeleteByUserIdAndPlatform(1, user.getId(), 1);

        // 生成新token
        String token = RandomStringUtils.randomAlphanumeric(32);

        UserTokenPO tokenPO = new UserTokenPO();
        tokenPO.setUserId(user.getId());
        tokenPO.setToken(token);
        tokenPO.setPlatform(1); // 1表示PC端
        tokenPO.setExpireTime(DateUtils.addDays(new Date(), 30));

        userTokenMapper.insertSelective(tokenPO);

        return token;
    }
}