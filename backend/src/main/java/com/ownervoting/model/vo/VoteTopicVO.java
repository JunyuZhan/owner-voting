package com.ownervoting.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VoteTopicVO {
    private Long id;
    private Long communityId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isAreaWeighted;
    private Boolean isRealName;
    private Boolean isResultPublic;
    private String status;
    private Long createdBy;
} 