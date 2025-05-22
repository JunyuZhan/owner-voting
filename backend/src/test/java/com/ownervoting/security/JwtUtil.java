package com.ownervoting.security;

import io.jsonwebtoken.Claims;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类的测试模拟实现
 */
@Component
@Profile("test")
public class JwtUtil {

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authorities);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        // 测试环境返回固定的测试令牌
        return "test-token-" + subject;
    }

    public String extractUsername(String token) {
        // 从测试令牌中提取用户名
        if (token.startsWith("test-token-")) {
            return token.substring("test-token-".length());
        }
        return null;
    }

    public Date extractExpiration(String token) {
        // 返回未来一小时的时间作为测试令牌过期时间
        return new Date(System.currentTimeMillis() + 3600000);
    }

    public Claims extractAllClaims(String token) {
        // 测试环境不需要实现此方法
        return null;
    }

    public Boolean isTokenExpired(String token) {
        // 测试令牌永不过期
        return false;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
} 