package com.ownervoting.controller;

import com.ownervoting.model.entity.SuggestionReply;
import com.ownervoting.service.SuggestionReplyService;
import com.ownervoting.model.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ownervoting.service.SuggestionService;
import com.ownervoting.service.AdminUserService;
import com.ownervoting.model.entity.Suggestion;
import com.ownervoting.model.entity.AdminUser;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/suggestion-reply")
public class SuggestionReplyController {

    @Autowired
    private SuggestionReplyService suggestionReplyService;

    @Autowired
    private SuggestionService suggestionService;

    @Autowired
    private AdminUserService adminUserService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public ApiResponse<SuggestionReply> addReply(@RequestBody SuggestionReply reply) {
        return ApiResponse.success(suggestionReplyService.addReply(reply));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteReply(@PathVariable Long id) {
        suggestionReplyService.deleteReply(id);
        return ApiResponse.success(null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ApiResponse<SuggestionReply> getById(@PathVariable Long id) {
        return ApiResponse.success(suggestionReplyService.findById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-suggestion/{suggestionId}")
    public ApiResponse<List<SuggestionReply>> getBySuggestionId(@PathVariable Long suggestionId) {
        return ApiResponse.success(suggestionReplyService.findBySuggestionId(suggestionId));
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','COMMUNITY_ADMIN','OPERATOR')")
    @PostMapping("/admin/suggestions/{id}/reply")
    public ApiResponse<SuggestionReply> adminReplySuggestion(@PathVariable Long id, @RequestBody SuggestionReply reply, @RequestParam Long adminId) {
        Suggestion suggestion = suggestionService.findById(id);
        AdminUser admin = adminUserService.findById(adminId);
        if (suggestion == null || admin == null) {
            return ApiResponse.error(404, "建议或管理员不存在");
        }
        reply.setSuggestion(suggestion);
        reply.setReplier(admin);
        return ApiResponse.success(suggestionReplyService.addReply(reply));
    }
} 