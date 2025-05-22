package com.ownervoting.service;

import com.ownervoting.model.entity.SystemLog;
import java.util.List;

public interface SystemLogService {
    SystemLog addLog(SystemLog log);
    void deleteLog(Long id);
    SystemLog findById(Long id);
    List<SystemLog> findByUserId(Long userId);
    List<SystemLog> findAll();
} 