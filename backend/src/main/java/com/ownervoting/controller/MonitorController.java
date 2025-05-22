package com.ownervoting.controller;

import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统监控控制器
 * 仅限系统管理员访问
 */
@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    /**
     * 获取系统基本信息
     */
    @GetMapping("/system")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Object>> getSystemInfo() {
        return ApiResponse.success(monitorService.getSystemInfo());
    }

    /**
     * 获取API请求统计
     */
    @GetMapping("/api-stats")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Object>> getApiStats() {
        return ApiResponse.success(monitorService.getApiStats());
    }

    /**
     * 获取系统错误统计
     */
    @GetMapping("/errors")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Object>> getErrorStats() {
        return ApiResponse.success(monitorService.getErrorStats());
    }

    /**
     * 获取缓存使用情况
     */
    @GetMapping("/cache")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Object>> getCacheStats() {
        return ApiResponse.success(monitorService.getCacheStats());
    }

    /**
     * 获取数据库操作统计
     */
    @GetMapping("/database")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Object>> getDatabaseStats() {
        return ApiResponse.success(monitorService.getDatabaseStats());
    }

    /**
     * 获取所有监控数据
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Object>> getAllStats() {
        Map<String, Object> allStats = new HashMap<>();
        allStats.put("system", monitorService.getSystemInfo());
        allStats.put("api", monitorService.getApiStats());
        allStats.put("errors", monitorService.getErrorStats());
        allStats.put("cache", monitorService.getCacheStats());
        allStats.put("database", monitorService.getDatabaseStats());
        return ApiResponse.success(allStats);
    }
} 