package com.ownervoting.model.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HouseVO {
    private Long id;
    private Long ownerId;
    private Long communityId;
    private String building;
    private String unit;
    private String room;
    private String address;
    private BigDecimal area;
    private String certificateNumber;
    private Boolean isPrimary;
} 