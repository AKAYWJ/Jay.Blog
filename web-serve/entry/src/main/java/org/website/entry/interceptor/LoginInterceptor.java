package org.website.entry.interceptor;

import com.alibaba.fastjson2.JSON;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;
import org.website.common.utils.JwtUtil;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.User;
import org.website.dao.mapper.util.UserContext;
import org.website.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT登录拦截器
 * 拦截所有需要登录的请求，验证token有效性
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    /**
     * 请求处理前执行
     * @return true：放行 false：拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 硬放行：图片预览路径，直接跳过所有登录校验
        String uri = request.getRequestURI();
        if (uri.startsWith("/upload/cover/")) {
            return true;
        }

        // 1. 处理OPTIONS跨域预检请求（直接放行）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 2. 从请求头获取token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // token不存在或格式错误
            return unauthorized(response, "请先登录");
        }

        // 3. 提取token（去掉"Bearer "前缀）
        String token = authHeader.substring(7);

        // 4. 验证token有效性
        if (!JwtUtil.validateToken(token)) {
            return unauthorized(response, "登录已过期，请重新登录");
        }

        // 5. 解析token获取用户ID
        Long userId;
        try {
            Claims claims = JwtUtil.parseToken(token);
            userId = Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return unauthorized(response, "无效的token");
        }

        // 6. 查询用户信息（验证用户是否存在、是否被禁用）
        User user = userService.getById(userId);
        if (user == null || !"Y".equals(user.getIsValid())) {
            return unauthorized(response, "账号不存在或已被禁用");
        }

        // 7. 将用户信息存入ThreadLocal，供后续业务使用
        UserContext.setUser(user);

        // 8. 放行请求
        return true;
    }

    /**
     * 请求处理完成后执行（无论成功失败）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal中的用户信息，防止内存泄漏
        UserContext.clear();
    }

    /**
     * 返回401未授权响应
     */
    private boolean unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        Result<?> result = Result.fail(401, message);
        response.getWriter().write(JSON.toJSONString(result));
        return false;
    }
}