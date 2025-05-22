package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class OwnerVerifyDTO {
    @NotNull(message = "业主ID不能为空")
    private Long ownerId;
    @NotNull(message = "身份证正面文件ID不能为空")
    private Long idCardFrontFileId;
    @NotNull(message = "身份证反面文件ID不能为空")
    private Long idCardBackFileId;
    private Long houseCertFileId;
    private String remark;
} 