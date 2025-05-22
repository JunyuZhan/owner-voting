package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class VoteTopicAddDTO {
    @NotNull(message = "小区ID不能为空")
    private Long communityId;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String description;
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    private Boolean isAreaWeighted = false;
    private Boolean isRealName = true;
    private Boolean isResultPublic = true;
    private String status;
    private Long createdBy;
} 