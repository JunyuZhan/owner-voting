package com.ownervoting.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置类
 * 本系统使用基于内存的缓存，生产环境可替换为Redis
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    public static final String VOTE_CACHE = "vote";
    public static final String USER_CACHE = "user";
    public static final String COMMUNITY_CACHE = "community";
    
    @Bean
    public CacheManager cacheManager() {
        // 使用基于内存的ConcurrentMap缓存，适合开发和小型应用
        // 注意：生产环境应考虑使用Redis等分布式缓存
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // 预先初始化缓存
        cacheManager.setCacheNames(java.util.Arrays.asList(
            VOTE_CACHE, 
            USER_CACHE, 
            COMMUNITY_CACHE
        ));
        
        return cacheManager;
    }
} 