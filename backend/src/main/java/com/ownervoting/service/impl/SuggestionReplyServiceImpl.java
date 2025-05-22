package com.ownervoting.service.impl;

import com.ownervoting.model.entity.SuggestionReply;
import com.ownervoting.repository.SuggestionReplyRepository;
import com.ownervoting.service.SuggestionReplyService;
import com.ownervoting.service.SystemLogService;
import com.ownervoting.model.entity.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SuggestionReplyServiceImpl implements SuggestionReplyService {

    @Autowired
    private SuggestionReplyRepository suggestionReplyRepository;

    @Autowired
    private SystemLogService systemLogService;

    @Override
    @Transactional
    public SuggestionReply addReply(SuggestionReply reply) {
        SuggestionReply saved = suggestionReplyRepository.save(reply);
        // 日志埋点
        SystemLog log = new SystemLog();
        log.setUserId(reply.getReplier() != null ? reply.getReplier().getId() : null);
        log.setUserType(SystemLog.UserType.ADMIN);
        log.setOperation("建议回复");
        log.setDetail("管理员回复建议，建议ID：" + (reply.getSuggestion() != null ? reply.getSuggestion().getId() : null));
        systemLogService.addLog(log);
        return saved;
    }

    @Override
    @Transactional
    public void deleteReply(Long id) {
        suggestionReplyRepository.deleteById(id);
    }

    @Override
    public SuggestionReply findById(Long id) {
        return suggestionReplyRepository.findById(id).orElse(null);
    }

    @Override
    public List<SuggestionReply> findBySuggestionId(Long suggestionId) {
        return suggestionReplyRepository.findAll().stream()
                .filter(r -> r.getSuggestion() != null && r.getSuggestion().getId().equals(suggestionId))
                .toList();
    }
} 