package org.website.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.common.result.Result;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图形验证码控制器（纯 SVG，不依赖任何图片库）
 * 验证码文本存在内存 Map，5 分钟过期，最多保留 1000 条
 */
@RestController
@RequestMapping("/auth")
public class CaptchaController {

    /** 验证码存储：id -> {code, expireAt} */
    private static final ConcurrentHashMap<String, Map<String, Object>> STORE = new ConcurrentHashMap<>();
    private static final long EXPIRE_MS = 5 * 60 * 1000L;
    private static final int MAX_SIZE = 1000;
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 排除易混淆字符 */
    private static final String CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    @GetMapping("/captcha")
    public Result<Map<String, Object>> captcha() {
        cleanExpired();

        String code = randomCode(4);
        String id = UUID.randomUUID().toString().replace("-", "");
        long expireAt = System.currentTimeMillis() + EXPIRE_MS;

        // Java 8 兼容：Map.of(K,V,K,V) 是 Java 9+，这里手写 HashMap
        Map<String, Object> entry = new HashMap<>();
        entry.put("code", code);
        entry.put("expireAt", expireAt);
        STORE.put(id, entry);

        String svg = buildSvg(code);

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("svg", svg);
        return Result.success(data);
    }

    /**
     * 校验验证码（供其他 controller 调用）
     * @return true 通过；false 失败（已消费）
     */
    public static boolean verify(String captchaId, String userInput) {
        if (captchaId == null || userInput == null) return false;
        Map<String, Object> entry = STORE.remove(captchaId);
        if (entry == null) return false;
        Object expireAtObj = entry.get("expireAt");
        if (!(expireAtObj instanceof Long)) return false;
        long expireAt = (Long) expireAtObj;
        if (System.currentTimeMillis() > expireAt) return false;
        Object codeObj = entry.get("code");
        if (!(codeObj instanceof String)) return false;
        String code = (String) codeObj;
        return code.equalsIgnoreCase(userInput.trim());
    }

    private static String randomCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private static void cleanExpired() {
        long now = System.currentTimeMillis();
        STORE.entrySet().removeIf(e -> {
            Object exp = e.getValue().get("expireAt");
            return exp instanceof Long && now > (Long) exp;
        });
        // 简单防膨胀
        if (STORE.size() > MAX_SIZE) {
            STORE.clear();
        }
    }

    /**
     * 生成 SVG 验证码图片
     * 160x60 画布，4 个字符带旋转 + 干扰线 + 干扰点
     */
    private static String buildSvg(String code) {
        StringBuilder svg = new StringBuilder(512);
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"110\" height=\"44\" viewBox=\"0 0 110 44\">");
        // 背景渐变
        svg.append("<defs><linearGradient id=\"bg\" x1=\"0\" y1=\"0\" x2=\"1\" y2=\"1\">")
                .append("<stop offset=\"0%\" stop-color=\"#faf5ff\"/>")
                .append("<stop offset=\"100%\" stop-color=\"#ede9fe\"/>")
                .append("</linearGradient></defs>");
        svg.append("<rect width=\"110\" height=\"44\" rx=\"6\" fill=\"url(#bg)\"/>");

        // 干扰线
        for (int i = 0; i < 4; i++) {
            int x1 = RANDOM.nextInt(110);
            int y1 = RANDOM.nextInt(44);
            int x2 = RANDOM.nextInt(110);
            int y2 = RANDOM.nextInt(44);
            String color = String.format("#%06x", RANDOM.nextInt(0x666666));
            svg.append("<line x1=\"").append(x1).append("\" y1=\"").append(y1)
                    .append("\" x2=\"").append(x2).append("\" y2=\"").append(y2)
                    .append("\" stroke=\"").append(color).append("\" stroke-width=\"1\" opacity=\"0.4\"/>");
        }

        // 干扰点
        for (int i = 0; i < 30; i++) {
            int x = RANDOM.nextInt(110);
            int y = RANDOM.nextInt(44);
            svg.append("<circle cx=\"").append(x).append("\" cy=\"").append(y)
                    .append("\" r=\"1\" fill=\"#5B4FCF\" opacity=\"0.3\"/>");
        }

        // 字符
        String[] colors = {"#5B4FCF", "#ec4899", "#06b6d4", "#f59e0b"};
        for (int i = 0; i < code.length(); i++) {
            int x = 14 + i * 24;
            int y = 30 + RANDOM.nextInt(6) - 3;
            int rotate = RANDOM.nextInt(40) - 20;
            String color = colors[RANDOM.nextInt(colors.length)];
            svg.append("<text x=\"").append(x).append("\" y=\"").append(y)
                    .append("\" font-family=\"Verdana, sans-serif\" font-size=\"22\" font-weight=\"bold\"")
                    .append(" fill=\"").append(color).append("\"")
                    .append(" transform=\"rotate(").append(rotate).append(" ").append(x).append(" ").append(y).append(")\">")
                    .append(code.charAt(i))
                    .append("</text>");
        }

        svg.append("</svg>");
        return svg.toString();
    }
}
