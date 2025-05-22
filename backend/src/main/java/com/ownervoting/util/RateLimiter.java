package com.ownervoting.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单的基于内存的限流器，使用令牌桶算法
 */
public class RateLimiter {
    
    // 存储每个IP的访问计数器 <IP, 计数器>
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    
    // 默认限流配置
    private final long defaultCapacity; // 桶容量
    private final long defaultRefillRate; // 每秒补充的令牌数
    
    public RateLimiter(long defaultCapacity, long defaultRefillRate) {
        this.defaultCapacity = defaultCapacity;
        this.defaultRefillRate = defaultRefillRate;
    }
    
    /**
     * 尝试获取令牌，判断请求是否允许通过
     * @param key 限流键（通常是IP地址）
     * @return 是否允许请求通过
     */
    public boolean tryAcquire(String key) {
        return buckets.computeIfAbsent(key, k -> new TokenBucket(defaultCapacity, defaultRefillRate))
                .tryConsume();
    }
    
    /**
     * 尝试获取令牌，判断请求是否允许通过
     * @param key 限流键（通常是IP地址）
     * @param tokenCount 需要消耗的令牌数量
     * @return 是否允许请求通过
     */
    public boolean tryAcquire(String key, int tokenCount) {
        return buckets.computeIfAbsent(key, k -> new TokenBucket(defaultCapacity, defaultRefillRate))
                .tryConsume(tokenCount);
    }
    
    /**
     * 清理过期的桶
     */
    public void cleanUp() {
        long now = System.currentTimeMillis();
        buckets.entrySet().removeIf(entry -> now - entry.getValue().getLastRefillTime() > TimeUnit.MINUTES.toMillis(30));
    }
    
    /**
     * 令牌桶实现
     */
    private static class TokenBucket {
        private final long capacity; // 桶容量
        private final double refillRate; // 每毫秒补充的令牌数
        private double tokens; // 当前令牌数
        private long lastRefillTime; // 上次补充令牌的时间
        
        public TokenBucket(long capacity, long refillRatePerSecond) {
            this.capacity = capacity;
            this.refillRate = (double) refillRatePerSecond / 1000; // 转换为每毫秒
            this.tokens = capacity; // 初始满额
            this.lastRefillTime = System.currentTimeMillis();
        }
        
        /**
         * 尝试消费一个令牌
         * @return 是否成功
         */
        public synchronized boolean tryConsume() {
            return tryConsume(1);
        }
        
        /**
         * 尝试消费多个令牌
         * @param count 令牌数量
         * @return 是否成功
         */
        public synchronized boolean tryConsume(int count) {
            refill(); // 先补充令牌
            
            if (tokens < count) {
                return false; // 令牌不足
            }
            
            tokens -= count;
            return true;
        }
        
        /**
         * 补充令牌
         */
        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;
            
            // 计算需要补充的令牌数
            double tokensToAdd = elapsed * refillRate;
            if (tokensToAdd > 0) {
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTime = now;
            }
        }
        
        public long getLastRefillTime() {
            return lastRefillTime;
        }
    }
} 