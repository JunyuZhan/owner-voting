package com.ownervoting.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.prefix:Bearer }")
    private String tokenPrefix;

    @Value("${security.jwt.token.header:Authorization}")
    private String headerName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // 检查JWT令牌
            String header = request.getHeader(headerName);
            
            if (header == null || !header.startsWith(tokenPrefix)) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // 除去令牌前缀
            String token = header.replace(tokenPrefix, "");
            
            try {
                // 验证令牌并提取声明
                Jws<Claims> claimsJws = Jwts.parser()
                        .setSigningKey(secretKey.getBytes())
                        .parseClaimsJws(token);
                
                Claims body = claimsJws.getBody();
                
                // 从令牌中提取用户名
                String username = body.getSubject();
                
                // 提取角色和权限
                @SuppressWarnings("unchecked")
                List<String> authorities = (List<String>) body.get("authorities");
                
                if (username != null) {
                    // 创建身份验证令牌并设置到SecurityContext
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                
            } catch (JwtException e) {
                logger.error("JWT验证失败", e);
                SecurityContextHolder.clearContext();
            }
            
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("JWT授权过滤器错误", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
} 