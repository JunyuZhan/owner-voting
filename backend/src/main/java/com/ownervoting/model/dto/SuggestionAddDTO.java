package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class SuggestionAddDTO {
    @NotNull(message = "小区ID不能为空")
    private Long communityId;
    @NotNull(message = "业主ID不能为空")
    private Long ownerId;
    @NotBlank(message = "标题不能为空")
    private String title;
    @NotBlank(message = "内容不能为空")
    private String content;
    private Boolean isAnonymous = false;
} 