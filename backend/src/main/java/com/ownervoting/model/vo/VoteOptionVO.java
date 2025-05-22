package com.ownervoting.model.vo;

import lombok.Data;

@Data
public class VoteOptionVO {
    private Long id;
    private Long topicId;
    private String optionText;
    private Integer sortOrder;
} 