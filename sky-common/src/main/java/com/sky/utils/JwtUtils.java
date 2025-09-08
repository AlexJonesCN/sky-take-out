package com.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * JWT（JSON Web Token）操作工具类.
 * 该类提供了生成和解析JWT令牌的静态方法。
 * 它使用HS256算法进行签名，并管理一个静态的、在应用生命周期内不变的密钥。
 */
public class JwtUtils {
	/**
	 * 私有构造函数，防止工具类被实例化。
	 */
	private JwtUtils() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	/**
     * 生成JWT令牌。
	 * @param secretKey 用于签名JWT的密钥字符串。
	 * @param ttlMillis 令牌的有效期，单位为毫秒。
     * @param claims 包含在JWT负载（payload）中的自定义声明（claims）,通常用于存放用户ID、用户名等非敏感信息。
	 * @return 生成的JWT令牌字符串。
     */
    public static String generateToken(String secretKey, long ttlMillis, Map<String, Object> claims) {
	    SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        // 设置过期时间为5小时后
        Instant expirationTime = now.plus(ttlMillis, ChronoUnit.MILLIS);
        return Jwts.builder()
                // 设置自定义声明
                .claims(claims)
                // 设置主题
                .subject("sky-take-out")
                // 设置签发时间
                .issuedAt(Date.from(now))
                // 设置过期时间
                .expiration(Date.from(expirationTime))
                // 设置生效时间（立即生效）
                .notBefore(Date.from(now))
                // 设置JWT的唯一标识（使用UUID保证唯一性）
                .id(java.util.UUID.randomUUID().toString())
                // 使用密钥进行签名
                .signWith(key)
                // 构建并序列化为紧凑的字符串格式
                .compact();
    }

    /**
	 * 解析并验证JWT令牌。
     * @param secretKey 用于验证JWT的密钥字符串。
	 * @param token 需要解析的JWT令牌字符串。
	 * @return 如果令牌有效，返回包含所有声明的Claims对象；如果无效，抛出JwtException异常。
	 */
	public static Claims parseToken(String secretKey, String token){
		if (token == null || token.isEmpty()) {
			return null;
		}
		// 解析并验证JWT令牌，如果无效或过期将抛出异常
		try {
			return Jwts.parser()
					.verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (JwtException e) {
			System.err.println("JWT parsing failed: " + e.getMessage());
			return null;
		}
	}
}
