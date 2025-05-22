package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class VoteOptionAddDTO {
    @NotNull(message = "议题ID不能为空")
    private Long topicId;
    @NotBlank(message = "选项内容不能为空")
    private String optionText;
    private Integer sortOrder = 0;
} 