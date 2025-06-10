package com.ownervoting.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 租户上下文管理器
 * 用于在整个请求过程中保存和获取当前用户的租户信息
 */
public class TenantContext {
    
    private static final ThreadLocal<Long> CURRENT_COMMUNITY_ID = new ThreadLocal<>();
    
    /**
     * 设置当前租户ID（小区ID）
     */
    public static void setCurrentCommunityId(Long communityId) {
        CURRENT_COMMUNITY_ID.set(communityId);
    }
    
    /**
     * 获取当前租户ID（小区ID）
     */
    public static Long getCurrentCommunityId() {
        return CURRENT_COMMUNITY_ID.get();
    }
    
    /**
     * 清除租户上下文
     */
    public static void clear() {
        CURRENT_COMMUNITY_ID.remove();
    }
    
    /**
     * 检查当前用户是否为系统管理员
     */
    public static boolean isSystemAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SYSTEM_ADMIN"));
    }
    
    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
} 