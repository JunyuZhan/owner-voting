package com.ownervoting.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SuggestionVO {
    private Long id;
    private Long communityId;
    private Long ownerId;
    private String title;
    private String content;
    private Boolean isAnonymous;
    private String status;
    private Integer likeCount;
    private Integer dislikeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
