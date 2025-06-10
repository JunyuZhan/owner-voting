package com.ownervoting.model.vo;

import lombok.Data;
import com.ownervoting.model.entity.House;
import java.util.List;

@Data
public class HouseConflictVO {
    
    // 是否存在冲突
    private Boolean hasConflict;
    
    // 冲突类型
    private ConflictType conflictType;
    
    // 冲突的房屋列表
    private List<House> conflictHouses;
    
    // 冲突描述
    private String conflictMessage;
    
    public enum ConflictType {
        NO_CONFLICT,        // 无冲突
        PENDING_APPROVAL,   // 有待审核的同位置房屋
        ALREADY_APPROVED,   // 已有认证通过的同位置房屋
        CERTIFICATE_DUPLICATE, // 房产证号重复
        MULTIPLE_DISPUTES   // 多个争议房屋
    }
    
    public static HouseConflictVO noConflict() {
        HouseConflictVO vo = new HouseConflictVO();
        vo.setHasConflict(false);
        vo.setConflictType(ConflictType.NO_CONFLICT);
        vo.setConflictMessage("无冲突");
        return vo;
    }
    
    public static HouseConflictVO conflict(ConflictType type, List<House> houses, String message) {
        HouseConflictVO vo = new HouseConflictVO();
        vo.setHasConflict(true);
        vo.setConflictType(type);
        vo.setConflictHouses(houses);
        vo.setConflictMessage(message);
        return vo;
    }
} 