package com.ownervoting.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // 使用与JwtUtil相同的密钥，确保一致性
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("ownervotingsecretkey123456789012345678901234567890".getBytes());

    @Value("${security.jwt.token.prefix:Bearer }")
    private String tokenPrefix;

    @Value("${security.jwt.token.header:Authorization}")
    private String headerName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        logger.info("处理请求: " + requestURI + ", 方法: " + request.getMethod());
        
        // 检查JWT令牌
        String header = request.getHeader(headerName);
        
        if (header == null || !header.startsWith(tokenPrefix)) {
            logger.info("请求没有JWT令牌或格式不正确: " + header);
            filterChain.doFilter(request, response);
            return;
        }
        
        // 除去令牌前缀
        String token = header.replace(tokenPrefix, "");
        logger.info("提取到JWT令牌: " + token.substring(0, Math.min(20, token.length())) + "...");
        
        try {
            // 验证令牌并提取声明
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            
            Claims body = claimsJws.getBody();
            
            // 从令牌中提取用户名
            String username = body.getSubject();
            
            // 提取角色和权限
            @SuppressWarnings("unchecked")
            List<String> authorities = (List<String>) body.get("authorities");
            
            logger.info("JWT验证成功 - 用户: " + username + ", 权限: " + authorities);
            
            if (username != null && authorities != null) {
                // 创建身份验证令牌并设置到SecurityContext
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                );
                
                SecurityContextHolder.getContext().setAuthentication(auth);
                logger.info("SecurityContext已设置，用户: " + username + ", 权限: " + auth.getAuthorities());
            } else {
                logger.warn("用户名或权限为空，未设置SecurityContext");
            }
            
        } catch (JwtException e) {
            logger.error("JWT验证失败: " + e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            logger.error("JWT解析异常: " + e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }
        
        filterChain.doFilter(request, response);
    }
}