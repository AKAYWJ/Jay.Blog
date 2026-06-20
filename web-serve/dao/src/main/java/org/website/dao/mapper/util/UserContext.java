package org.website.dao.mapper.util;


import org.website.dao.mapper.entity.User;

/**
 * 用户上下文工具类
 * 基于ThreadLocal实现，每个请求线程独立存储用户信息
 */
public class UserContext {
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前登录用户
     */
    public static void setUser(User user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取当前登录用户
     */
    public static User getUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前登录用户ID
     */
    public static Integer getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        User user = getUser();
        return user != null ? user.getName() : null;
    }

    /**
     * 清除当前线程用户信息（防止内存泄漏）
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
}