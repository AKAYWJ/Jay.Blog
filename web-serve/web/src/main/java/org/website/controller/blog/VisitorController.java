package org.website.controller.blog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.website.common.annotation.RequiresPermission;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.Visitor;
import org.website.service.VisitorService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 访客统计控制器
 */
@RestController
@RequestMapping("/visitor")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    // 分页访客列表
    @GetMapping("/list")
    @RequiresPermission("visitor:list")
    public Result<Page<Visitor>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String ip) {

        QueryWrapper<Visitor> wrapper = new QueryWrapper<>();
        if (ip != null && !ip.isEmpty()) wrapper.like("ip", ip);

        Page<Visitor> page = visitorService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    /**
     * 前台访问首页新增访客记录
     * 规则：同一IP 同一天仅保存一条记录，不重复新增
     */
    @PostMapping("/front/add")
    public Result<String> addVisitor() {
        HttpServletRequest request = getHttpServletRequest();
        String ip = getIpAddr(request);
        if (ip == null || ip.isEmpty()) {
            return Result.fail(400,"获取访客IP失败");
        }

        LocalDate today = LocalDate.now();
        QueryWrapper<Visitor> wrapper = new QueryWrapper<>();
        wrapper.eq("ip", ip);
        // 筛选今天的访问记录
        wrapper.apply("DATE(visit_time) = {0}", today);
        long count = visitorService.count(wrapper);

        // 今日无此IP记录，新增
        if (count == 0) {
            Visitor visitor = new Visitor();
            visitor.setIp(ip);
            visitor.setVisitTime(LocalDateTime.now());
            // 地址这里先填空，可后续集成IP地址解析工具
            visitor.setAddress("");
            visitorService.save(visitor);
        }
        return Result.success("ok");
    }

    // 获取request
    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    // 获取真实客户端IP（兼容nginx反向代理）
    private String getIpAddr(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        String proxyIp = request.getHeader("Proxy-Client-IP");
        if (proxyIp != null && !proxyIp.isEmpty() && !"unknown".equalsIgnoreCase(proxyIp)) {
            return proxyIp;
        }
        String wlProxyIp = request.getHeader("WL-Proxy-Client-IP");
        if (wlProxyIp != null && !wlProxyIp.isEmpty() && !"unknown".equalsIgnoreCase(wlProxyIp)) {
            return wlProxyIp;
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取每日访客折线数据
     */
    @GetMapping("/front/daily")
    public Result<List<Map<String, Object>>> getDailyVisitor() {
        QueryWrapper<Visitor> wrapper = new QueryWrapper<>();
        // 按日期分组，格式化日期，统计当天访客数
        wrapper.select("DATE_FORMAT(visit_time,'%m.%d') as day, COUNT(DISTINCT ip) as count")
                .groupBy("day")
                .orderByAsc("day");
        List<Map<String, Object>> list = visitorService.listMaps(wrapper);
        return Result.success(list);
    }
}