package com.ownervoting.service;

import com.ownervoting.model.entity.Suggestion;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SuggestionService {
    Suggestion addSuggestion(Suggestion suggestion);
    void deleteSuggestion(Long id);
    Suggestion findById(Long id);
    List<Suggestion> findByOwnerId(Long ownerId);
    Page<Suggestion> findPage(Pageable pageable);
} 