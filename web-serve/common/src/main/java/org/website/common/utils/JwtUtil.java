package org.website.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * JWT工具类（兼容Java 8，JJWT 0.9.1版本）
 */
public class JwtUtil {
    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY") != null
            ? System.getenv("JWT_SECRET_KEY")
            : "AKAYWJ-WebSystem-20260609-SecretKey123456";
    // token过期时间：7天
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 生成token
     */
    public static String generateToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(userId.toString()) // 主题：用户ID
                .claim("username", username) // 自定义载荷：用户名
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 签名算法
                .compact();
    }

    /**
     * 解析token
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证token是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从token中获取用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }
}