package com.ownervoting.interceptor;

import com.ownervoting.service.MonitorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 性能监控拦截器
 * 用于记录请求处理时间和异常情况
 */
@Component
public class PerformanceInterceptor implements HandlerInterceptor {

    @Autowired
    private MonitorService monitorService;
    
    private static final String START_TIME_ATTR = "request_start_time";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 不执行任何操作
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 计算请求处理时间
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        if (startTime != null) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 记录请求URI和处理时间
            String uri = request.getRequestURI();
            monitorService.recordApiRequest(uri, duration);
        }
        
        // 记录异常
        if (ex != null) {
            monitorService.recordError(ex.getClass().getName(), ex.getMessage());
        }
    }
} 