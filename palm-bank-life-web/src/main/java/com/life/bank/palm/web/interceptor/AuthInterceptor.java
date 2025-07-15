package com.life.bank.palm.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.life.bank.palm.common.context.UserContext;
import com.life.bank.palm.common.exception.BaseBizCodeEnum;
import com.life.bank.palm.common.result.CommonResponse;
import com.life.bank.palm.dao.user.mapper.UserMapper;
import com.life.bank.palm.dao.user.mapper.UserTokenMapper;
import com.life.bank.palm.dao.user.pojo.UserPO;
import com.life.bank.palm.dao.user.pojo.UserTokenPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从header中获取token
        String token = request.getHeader("Authorization");

        if (StringUtils.isBlank(token)) {
            // 尝试从参数中获取token
            token = request.getParameter("token");
        }

        if (StringUtils.isBlank(token)) {
            writeErrorResponse(response, "未登录");
            return false;
        }

        // 查询token信息
        UserTokenPO userToken = userTokenMapper.selectOneByTokenAndIsDelete(token, 0);

        if (userToken == null) {
            writeErrorResponse(response, "token无效");
            return false;
        }

        // 检查token是否过期
        if (userToken.getExpireTime().before(new Date())) {
            writeErrorResponse(response, "token已过期");
            return false;
        }

        // 查询用户信息
        UserPO user = userMapper.selectOneByIdAndIsDelete(userToken.getUserId(), 0);
        if (user == null) {
            writeErrorResponse(response, "用户不存在");
            return false;
        }

        // 设置用户上下文
        UserContext.UserInfo userInfo = new UserContext.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setPhone(user.getPhone());
        userInfo.setNickname(user.getNickname());
        userInfo.setCardId(user.getCardId());
        UserContext.setUser(userInfo);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal
        UserContext.clear();
    }

    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        CommonResponse<Object> errorResponse = CommonResponse.buildError(
                BaseBizCodeEnum.NOT_LOGIN.getErrorCode(),
                message
        );

        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(errorResponse));
        writer.flush();
        writer.close();
    }
}