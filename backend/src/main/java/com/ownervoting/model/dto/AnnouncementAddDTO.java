package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class AnnouncementAddDTO {
    @NotNull(message = "小区ID不能为空")
    private Long communityId;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String content;
    @NotBlank(message = "类型不能为空")
    private String type;
    private Boolean isPinned = false;
    private LocalDateTime publishedAt;
    private Long createdBy;
} 