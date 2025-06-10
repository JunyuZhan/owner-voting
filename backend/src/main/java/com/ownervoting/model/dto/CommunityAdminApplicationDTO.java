package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Data
public class CommunityAdminApplicationDTO {
    
    // 申请人信息
    @NotBlank(message = "申请人姓名不能为空")
    private String applicantName;
    
    @NotBlank(message = "申请人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String applicantPhone;
    
    @Email(message = "邮箱格式不正确")
    private String applicantEmail;
    
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", 
             message = "身份证号格式不正确")
    private String applicantIdCard;
    
    // 小区信息
    @NotBlank(message = "小区名称不能为空")
    private String communityName;
    
    @NotBlank(message = "小区地址不能为空")
    private String communityAddress;
    
    private String communityDescription;
    
    // 申请理由和资质证明
    @NotBlank(message = "申请理由不能为空")
    private String applicationReason;
    
    private String qualificationProof;
    
    private String businessLicense;
} 