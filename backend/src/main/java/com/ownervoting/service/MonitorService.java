package com.ownervoting.service;

import java.util.Map;

/**
 * 系统监控服务接口
 * 用于采集系统性能指标和运行状态
 */
public interface MonitorService {
    /**
     * 获取系统基本信息
     * 包括JVM内存使用、CPU使用率、运行时长等
     */
    Map<String, Object> getSystemInfo();
    
    /**
     * 获取最近的API请求统计
     * 包括请求量、响应时间分布等
     */
    Map<String, Object> getApiStats();
    
    /**
     * 获取错误统计信息
     * 包括异常分布、错误率等
     */
    Map<String, Object> getErrorStats();
    
    /**
     * 获取数据库操作统计
     * 包括查询次数、平均响应时间等
     */
    Map<String, Object> getDatabaseStats();
    
    /**
     * 获取缓存统计信息
     * 包括命中率、使用量等
     */
    Map<String, Object> getCacheStats();
    
    /**
     * 记录请求处理时间
     * @param uri 请求URI
     * @param timeMs 处理时间(毫秒)
     */
    void recordApiRequest(String uri, long timeMs);
    
    /**
     * 记录错误发生
     * @param errorType 错误类型
     * @param message 错误消息
     */
    void recordError(String errorType, String message);
} 