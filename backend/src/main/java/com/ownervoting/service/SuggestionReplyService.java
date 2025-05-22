package com.ownervoting.service;

import com.ownervoting.model.entity.SuggestionReply;
import java.util.List;

public interface SuggestionReplyService {
    SuggestionReply addReply(SuggestionReply reply);
    void deleteReply(Long id);
    SuggestionReply findById(Long id);
    List<SuggestionReply> findBySuggestionId(Long suggestionId);
} 