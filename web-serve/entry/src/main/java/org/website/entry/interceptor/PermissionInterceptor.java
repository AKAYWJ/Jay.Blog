package org.website.entry.interceptor;

import com.alibaba.fastjson2.JSON;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.website.common.annotation.RequiresPermission;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.SysPermission;
import org.website.dao.mapper.entity.User;
import org.website.dao.mapper.util.UserContext;
import org.website.service.RolePermissionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

public class PermissionInterceptor implements HandlerInterceptor {

    @Resource
    private RolePermissionService rolePermissionService;

    // 修复：移除通配符**，使用纯前缀匹配
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/register",
            "/auth/info",
            "/auth/id",
            "/static/",
            "/favicon.ico"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 硬放行图片预览地址，直接return true，不走后面任何校验
        if(uri.contains("/upload/cover/")){
            return true;
        }
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String requestUri = request.getRequestURI();
        for (String whiteUri : WHITE_LIST) {
            if (requestUri.startsWith(whiteUri)) {
                return true;
            }
        }

        User user = UserContext.getUser();
        if (user == null) {
            return writeError(response, 401, "请先登录");
        }

        // 管理员直接放行
        if (Integer.valueOf(1).equals(user.getRoleId())) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequiresPermission annotation = handlerMethod.getMethodAnnotation(RequiresPermission.class);

        if (annotation == null) {
            return true;
        }

        String requiredPermission = annotation.value();
        List<String> userPermissions = rolePermissionService.getPermissionsByRoleId(user.getRoleId())
                .stream()
                .map(SysPermission::getCode)
                .collect(Collectors.toList());

        if (userPermissions.contains(requiredPermission)) {
            return true;
        } else {
            return writeError(response, 403, "权限不足，无法访问");
        }
    }

    private boolean writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        response.getWriter().write(JSON.toJSONString(Result.fail(code, message)));
        return false;
    }
}