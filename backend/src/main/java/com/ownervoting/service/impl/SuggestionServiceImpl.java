package com.ownervoting.service.impl;

import com.ownervoting.model.entity.Suggestion;
import com.ownervoting.repository.SuggestionRepository;
import com.ownervoting.service.SuggestionService;
import com.ownervoting.service.SystemLogService;
import com.ownervoting.model.entity.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SuggestionServiceImpl implements SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private SystemLogService systemLogService;

    @Override
    @Transactional
    public Suggestion addSuggestion(Suggestion suggestion) {
        Suggestion saved = suggestionRepository.save(suggestion);
        // 日志埋点
        SystemLog log = new SystemLog();
        log.setUserId(suggestion.getOwner() != null ? suggestion.getOwner().getId() : null);
        log.setUserType(SystemLog.UserType.OWNER);
        log.setOperation("建议提交");
        log.setDetail("用户提交建议，标题：" + suggestion.getTitle());
        systemLogService.addLog(log);
        return saved;
    }

    @Override
    @Transactional
    public void deleteSuggestion(Long id) {
        suggestionRepository.deleteById(id);
    }

    @Override
    public Suggestion findById(Long id) {
        if (id == null) {
            return null;
        }
        return suggestionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Suggestion> findByOwnerId(Long ownerId) {
        if (ownerId == null) {
            return List.of();
        }
        return suggestionRepository.findByOwnerId(ownerId);
    }

    @Override
    public Page<Suggestion> findPage(Pageable pageable) {
        return suggestionRepository.findAll(pageable);
    }
}