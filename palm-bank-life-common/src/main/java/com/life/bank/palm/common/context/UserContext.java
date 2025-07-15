package com.life.bank.palm.common.context;

import lombok.Data;

/**
 * 用户上下文，使用ThreadLocal存储当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(UserInfo userInfo) {
        USER_THREAD_LOCAL.set(userInfo);
    }

    public static UserInfo getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static Integer getUserId() {
        UserInfo userInfo = USER_THREAD_LOCAL.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }

    @Data
    public static class UserInfo {
        private Integer userId;
        private String phone;
        private String nickname;
        private String cardId;
    }
}