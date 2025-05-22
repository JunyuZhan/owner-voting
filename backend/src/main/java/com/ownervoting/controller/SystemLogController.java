package com.ownervoting.controller;

import com.ownervoting.model.entity.SystemLog;
import com.ownervoting.service.SystemLogService;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.dto.SystemLogAddDTO;
import com.ownervoting.model.vo.SystemLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/system-log")
@Validated
public class SystemLogController {

    @Autowired
    private SystemLogService systemLogService;

    @PostMapping("/add")
    public ApiResponse<SystemLogVO> addLog(@Valid @RequestBody SystemLogAddDTO dto) {
        SystemLog log = new SystemLog();
        log.setUserId(dto.getUserId());
        log.setUserType(SystemLog.UserType.valueOf(dto.getUserType()));
        log.setOperation(dto.getOperation());
        log.setIpAddress(dto.getIpAddress());
        log.setDetail(dto.getDetail());
        SystemLog saved = systemLogService.addLog(log);
        SystemLogVO vo = toVO(saved);
        return ApiResponse.success(vo);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteLog(@PathVariable Long id) {
        systemLogService.deleteLog(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<SystemLogVO> getById(@PathVariable Long id) {
        SystemLog log = systemLogService.findById(id);
        if (log == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(log));
    }

    @GetMapping("/by-user/{userId}")
    public ApiResponse<List<SystemLogVO>> getByUserId(@PathVariable Long userId) {
        List<SystemLog> list = systemLogService.findByUserId(userId);
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    @GetMapping("/all")
    public ApiResponse<List<SystemLogVO>> getAll() {
        List<SystemLog> list = systemLogService.findAll();
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    private SystemLogVO toVO(SystemLog l) {
        SystemLogVO vo = new SystemLogVO();
        vo.setId(l.getId());
        vo.setUserId(l.getUserId());
        vo.setUserType(l.getUserType() != null ? l.getUserType().name() : null);
        vo.setOperation(l.getOperation());
        vo.setIpAddress(l.getIpAddress());
        vo.setDetail(l.getDetail());
        vo.setCreatedAt(l.getCreatedAt());
        return vo;
    }
} 