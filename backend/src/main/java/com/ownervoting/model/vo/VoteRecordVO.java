package com.ownervoting.model.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VoteRecordVO {
    private Long id;
    private Long topicId;
    private Long optionId;
    private String optionText;
    private Long houseId;
    private String houseAddress;
    private Long voterId;
    private String voterName;
    private BigDecimal voteWeight;
    private LocalDateTime voteTime;
    private String ipAddress;
    private String deviceInfo;
} 