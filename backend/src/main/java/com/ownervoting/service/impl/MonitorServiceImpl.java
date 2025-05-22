package com.ownervoting.service.impl;

import com.ownervoting.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Queue;

@Service
public class MonitorServiceImpl implements MonitorService {
    
    private final ConcurrentHashMap<String, ApiStats> apiStatsMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> errorStatsMap = new ConcurrentHashMap<>();
    
    private final Queue<ErrorRecord> recentErrors = new LinkedList<>();
    private static final int MAX_RECENT_ERRORS = 100;
    
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    
    @Autowired(required = false)
    private CacheManager cacheManager;
    
    private static class ApiStats {
        AtomicLong requestCount = new AtomicLong(0);
        AtomicLong totalTimeMs = new AtomicLong(0);
        AtomicLong maxTimeMs = new AtomicLong(0);
        Queue<Long> recentTimes = new LinkedList<>();
        static final int MAX_RECENT_TIMES = 100;
        
        void recordRequest(long timeMs) {
            requestCount.incrementAndGet();
            totalTimeMs.addAndGet(timeMs);
            
            // 更新最大响应时间
            long current = maxTimeMs.get();
            while (timeMs > current) {
                if (maxTimeMs.compareAndSet(current, timeMs)) {
                    break;
                }
                current = maxTimeMs.get();
            }
            
            // 记录最近的响应时间
            synchronized (recentTimes) {
                recentTimes.add(timeMs);
                while (recentTimes.size() > MAX_RECENT_TIMES) {
                    recentTimes.poll();
                }
            }
        }
        
        Map<String, Object> getStats() {
            Map<String, Object> stats = new HashMap<>();
            stats.put("count", requestCount.get());
            
            long count = requestCount.get();
            if (count > 0) {
                stats.put("avgTimeMs", totalTimeMs.get() / count);
                stats.put("maxTimeMs", maxTimeMs.get());
                
                // 计算最近请求的平均时间
                synchronized (recentTimes) {
                    if (!recentTimes.isEmpty()) {
                        double recentAvg = recentTimes.stream()
                                .mapToLong(Long::longValue)
                                .average()
                                .orElse(0);
                        stats.put("recentAvgTimeMs", recentAvg);
                    }
                }
            }
            
            return stats;
        }
    }
    
    private static class ErrorRecord {
        final String type;
        final String message;
        final long timestamp;
        
        ErrorRecord(String type, String message) {
            this.type = type;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    @Override
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // JVM内存信息
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        Map<String, Object> memory = new HashMap<>();
        memory.put("heapUsed", memoryBean.getHeapMemoryUsage().getUsed());
        memory.put("heapMax", memoryBean.getHeapMemoryUsage().getMax());
        memory.put("nonHeapUsed", memoryBean.getNonHeapMemoryUsage().getUsed());
        info.put("memory", memory);
        
        // 运行时信息
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        Map<String, Object> runtime = new HashMap<>();
        runtime.put("uptime", Duration.ofMillis(runtimeBean.getUptime()).toString());
        runtime.put("startTime", runtimeBean.getStartTime());
        info.put("runtime", runtime);
        
        // 请求统计
        info.put("totalRequests", totalRequests.get());
        info.put("totalErrors", totalErrors.get());
        if (totalRequests.get() > 0) {
            double errorRate = (double) totalErrors.get() / totalRequests.get() * 100;
            info.put("errorRate", String.format("%.2f%%", errorRate));
        }
        
        return info;
    }
    
    @Override
    public Map<String, Object> getApiStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 所有API的统计信息
        Map<String, Object> apiStats = apiStatsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getStats()
                ));
        stats.put("endpoints", apiStats);
        
        // 总体API统计
        stats.put("totalRequests", totalRequests.get());
        
        // 计算总体平均响应时间
        long totalTime = apiStatsMap.values().stream()
                .mapToLong(v -> v.totalTimeMs.get())
                .sum();
        if (totalRequests.get() > 0) {
            stats.put("overallAvgTimeMs", totalTime / totalRequests.get());
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getErrorStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 错误类型分布
        Map<String, Integer> errorCounts = errorStatsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().get()
                ));
        stats.put("byType", errorCounts);
        
        // 总错误数和错误率
        stats.put("totalErrors", totalErrors.get());
        if (totalRequests.get() > 0) {
            double errorRate = (double) totalErrors.get() / totalRequests.get() * 100;
            stats.put("errorRate", String.format("%.2f%%", errorRate));
        }
        
        // 最近的错误记录
        List<Map<String, Object>> recent;
        synchronized (recentErrors) {
            recent = recentErrors.stream().map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("type", e.type);
                map.put("message", e.message);
                map.put("timestamp", e.timestamp);
                return map;
            }).collect(Collectors.toList());
        }
        stats.put("recent", recent);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getDatabaseStats() {
        // 实际实现可能需要结合数据库监控工具
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Database statistics monitoring not implemented yet");
        return stats;
    }
    
    @Override
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        if (cacheManager != null) {
            // 获取所有缓存名称
            Map<String, Object> caches = new HashMap<>();
            cacheManager.getCacheNames().forEach(name -> {
                caches.put(name, Map.of("available", true));
            });
            stats.put("caches", caches);
        } else {
            stats.put("message", "No cache manager available");
        }
        
        return stats;
    }
    
    @Override
    public void recordApiRequest(String uri, long timeMs) {
        totalRequests.incrementAndGet();
        
        apiStatsMap.computeIfAbsent(uri, k -> new ApiStats())
                .recordRequest(timeMs);
    }
    
    @Override
    public void recordError(String errorType, String message) {
        totalErrors.incrementAndGet();
        
        // 更新错误类型计数
        errorStatsMap.computeIfAbsent(errorType, k -> new AtomicInteger(0))
                .incrementAndGet();
        
        // 记录最近错误
        synchronized (recentErrors) {
            recentErrors.add(new ErrorRecord(errorType, message));
            while (recentErrors.size() > MAX_RECENT_ERRORS) {
                recentErrors.poll();
            }
        }
    }
} 