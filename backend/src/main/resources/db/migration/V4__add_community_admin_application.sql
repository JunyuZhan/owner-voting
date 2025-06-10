-- 创建小区管理员申请表
CREATE TABLE community_admin_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 申请人信息
    applicant_name VARCHAR(50) NOT NULL COMMENT '申请人姓名',
    applicant_phone VARCHAR(20) NOT NULL COMMENT '申请人手机号',
    applicant_email VARCHAR(100) COMMENT '申请人邮箱',
    applicant_id_card VARCHAR(18) COMMENT '申请人身份证号',
    
    -- 小区信息（申请创建的小区）
    community_name VARCHAR(255) NOT NULL COMMENT '小区名称',
    community_address VARCHAR(255) NOT NULL COMMENT '小区地址',
    community_description TEXT COMMENT '小区描述',
    
    -- 申请理由和资质证明
    application_reason TEXT NOT NULL COMMENT '申请理由',
    qualification_proof TEXT COMMENT '资质证明材料说明',
    business_license VARCHAR(255) COMMENT '营业执照或相关证件',
    
    -- 申请状态
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '申请状态',
    
    -- 审核信息
    reviewed_at DATETIME COMMENT '审核时间',
    reviewer_name VARCHAR(50) COMMENT '审核人',
    review_remark TEXT COMMENT '审核备注',
    
    -- 创建的小区ID（审核通过后）
    created_community_id BIGINT COMMENT '创建的小区ID',
    
    -- 创建的管理员用户ID（审核通过后）
    created_admin_user_id BIGINT COMMENT '创建的管理员用户ID',
    
    -- 时间戳
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 外键约束
    FOREIGN KEY (created_community_id) REFERENCES community(id),
    FOREIGN KEY (created_admin_user_id) REFERENCES admin_user(id),
    
    -- 索引
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_applicant_phone (applicant_phone)
) COMMENT '小区管理员申请表'; 