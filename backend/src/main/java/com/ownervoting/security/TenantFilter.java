package com.ownervoting.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ownervoting.model.entity.AdminUser;
import com.ownervoting.service.AdminUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 租户过滤器
 * 在每个请求开始时设置当前用户的租户上下文
 */
@Component
public class TenantFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);
    
    @Autowired
    private AdminUserService adminUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                String username = auth.getName();
                logger.debug("设置租户上下文 - 用户: {}", username);
                
                // 根据用户名查找管理员信息
                AdminUser adminUser = adminUserService.findByUsername(username);
                if (adminUser != null && adminUser.getCommunity() != null) {
                    TenantContext.setCurrentCommunityId(adminUser.getCommunity().getId());
                    logger.debug("租户上下文设置成功 - 用户: {}, 小区ID: {}", username, adminUser.getCommunity().getId());
                } else {
                    // 系统管理员或没有绑定小区的管理员，不设置租户ID
                    logger.debug("用户 {} 未绑定小区或为系统管理员", username);
                }
            }
            
            filterChain.doFilter(request, response);
            
        } finally {
            // 请求结束后清除租户上下文
            TenantContext.clear();
        }
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // 登录接口不需要租户过滤
        return path.contains("/auth/") || path.contains("/error") || 
               path.contains("/swagger") || path.contains("/v3/api-docs");
    }
} 