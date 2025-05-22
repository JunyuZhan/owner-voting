package com.ownervoting.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementVO {
    private Long id;
    private Long communityId;
    private String title;
    private String content;
    private String type;
    private Boolean isPinned;
    private LocalDateTime publishedAt;
    private Long createdBy;
} 