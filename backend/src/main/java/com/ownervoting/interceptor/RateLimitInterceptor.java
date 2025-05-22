package com.ownervoting.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.util.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 请求限流拦截器
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiter commonLimiter; // 普通接口限流器
    private final RateLimiter uploadLimiter; // 文件上传限流器
    private final RateLimiter voteLimiter;   // 投票接口限流器
    private final ObjectMapper objectMapper;
    
    @Autowired
    public RateLimitInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        
        // 创建不同接口的限流器，配置不同的限流策略
        // 普通接口：每个IP每秒50个请求，桶容量100
        this.commonLimiter = new RateLimiter(100, 50);
        
        // 文件上传接口：每个IP每秒5个请求，桶容量10
        this.uploadLimiter = new RateLimiter(10, 5);
        
        // 投票接口：每个IP每秒10个请求，桶容量20
        this.voteLimiter = new RateLimiter(20, 10);
        
        // 启动清理任务
        startCleanupTask();
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = getClientIp(request);
        String requestURI = request.getRequestURI();
        
        // 根据请求类型选择相应的限流器
        RateLimiter limiter = selectLimiter(requestURI);
        
        // 尝试获取令牌
        if (!limiter.tryAcquire(ip)) {
            handleRateLimitExceeded(response);
            return false;
        }
        
        return true;
    }
    
    /**
     * 根据URI选择对应的限流器
     */
    private RateLimiter selectLimiter(String uri) {
        if (uri.contains("/upload/")) {
            return uploadLimiter; // 文件上传接口
        } else if (uri.contains("/votes/") && uri.contains("/cast")) {
            return voteLimiter; // 投票接口
        } else {
            return commonLimiter; // 普通接口
        }
    }
    
    /**
     * 处理超出限流的请求
     */
    private void handleRateLimitExceeded(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        ApiResponse<?> apiResponse = ApiResponse.error(429, "请求频率过高，请稍后再试");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
    
    /**
     * 获取客户端真实IP
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // 多次代理后会有多个IP值，第一个为真实IP
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 启动定期清理任务
     */
    private void startCleanupTask() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    // 每10分钟清理一次
                    TimeUnit.MINUTES.sleep(10);
                    commonLimiter.cleanUp();
                    uploadLimiter.cleanUp();
                    voteLimiter.cleanUp();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.setName("rate-limiter-cleanup");
        cleanupThread.start();
    }
}