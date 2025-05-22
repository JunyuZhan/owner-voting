package com.ownervoting.service.impl;

import com.ownervoting.model.entity.SystemLog;
import com.ownervoting.repository.SystemLogRepository;
import com.ownervoting.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SystemLogServiceImpl implements SystemLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    @Transactional
    public SystemLog addLog(SystemLog log) {
        return systemLogRepository.save(log);
    }

    @Override
    @Transactional
    public void deleteLog(Long id) {
        systemLogRepository.deleteById(id);
    }

    @Override
    public SystemLog findById(Long id) {
        return systemLogRepository.findById(id).orElse(null);
    }

    @Override
    public List<SystemLog> findByUserId(Long userId) {
        return systemLogRepository.findAll().stream()
                .filter(l -> l.getUserId() != null && l.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<SystemLog> findAll() {
        return systemLogRepository.findAll();
    }
}
