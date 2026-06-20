package org.website.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 密码加密工具类（MD5 + Base64 加密）
 * 无任何第三方依赖，直接使用
 */
public class PasswordUtil {

    /**
     * 密码加密（MD5）
     */
    public static String encode(String rawPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败");
        }
    }

    /**
     * 密码校验
     */
    public static boolean match(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}