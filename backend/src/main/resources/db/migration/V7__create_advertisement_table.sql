-- 创建广告表
CREATE TABLE advertisement (
    id BIGINT IDENTITY PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    image_url VARCHAR(500),
    link_url VARCHAR(500),
    baidu_cpro_id VARCHAR(100),
    tencent_app_id VARCHAR(100),
    tencent_placement_id VARCHAR(100),
    bytedance_app_id VARCHAR(100),
    bytedance_slot_id VARCHAR(100),
    alimama_pid VARCHAR(100),
    qihoo_pos_id VARCHAR(100),
    sogou_app_id VARCHAR(100),
    jd_union_id VARCHAR(100),
    pdd_app_key VARCHAR(100),
    width INT,
    height INT,
    weight INT NOT NULL DEFAULT 10,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    click_count BIGINT DEFAULT 0,
    view_count BIGINT DEFAULT 0,
    created_by VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_advertisement_type ON advertisement (type);
CREATE INDEX idx_advertisement_active ON advertisement (is_active);
CREATE INDEX idx_advertisement_weight ON advertisement (weight);
CREATE INDEX idx_advertisement_time ON advertisement (start_time, end_time);

-- 插入示例数据
INSERT INTO advertisement (title, type, image_url, link_url, weight, is_active, description, created_by) VALUES
('物业管理系统专业版', 'BANNER', 'https://via.placeholder.com/300x100?text=物业管理系统', 'https://example.com/property-management', 15, TRUE, '专业的物业管理解决方案', 'system'),
('智能门禁系统', 'BANNER', 'https://via.placeholder.com/300x100?text=智能门禁', 'https://example.com/smart-access', 12, TRUE, '安全便捷的智能门禁解决方案', 'system'),
('小区安防监控', 'BANNER', 'https://via.placeholder.com/300x100?text=安防监控', 'https://example.com/security', 10, TRUE, '全方位的小区安防监控系统', 'system'),
('百度联盟广告示例', 'BAIDU', NULL, NULL, 20, TRUE, '百度联盟广告展示位', 'system'),
('腾讯优量汇广告示例', 'TENCENT', NULL, NULL, 18, TRUE, '腾讯优量汇广告展示位', 'system'),
('字节跳动穿山甲广告示例', 'BYTEDANCE', NULL, NULL, 16, TRUE, '穿山甲广告展示位', 'system'),
('阿里妈妈广告示例', 'ALIMAMA', NULL, NULL, 14, TRUE, '阿里妈妈广告展示位', 'system'),
('360广告示例', 'QIHOO', NULL, NULL, 12, TRUE, '360广告展示位', 'system'),
('搜狗广告示例', 'SOGOU', NULL, NULL, 10, TRUE, '搜狗广告展示位', 'system'),
('京东联盟广告示例', 'JD', NULL, NULL, 8, TRUE, '京东联盟广告展示位', 'system'),
('拼多多推广示例', 'PDD', NULL, NULL, 6, TRUE, '拼多多推广展示位', 'system');

-- 更新百度联盟广告的配置
UPDATE advertisement SET 
    baidu_cpro_id = 'u1234567',
    width = 300,
    height = 100
WHERE type = 'BAIDU';

-- 更新腾讯优量汇广告的配置
UPDATE advertisement SET 
    tencent_app_id = '1234567890',
    tencent_placement_id = '9876543210',
    width = 320,
    height = 100
WHERE type = 'TENCENT';

-- 更新字节跳动穿山甲广告的配置
UPDATE advertisement SET 
    bytedance_app_id = '5001234567',
    bytedance_slot_id = '901234567',
    width = 300,
    height = 100
WHERE type = 'BYTEDANCE';

-- 更新阿里妈妈广告的配置
UPDATE advertisement SET 
    alimama_pid = 'mm_123456_1234_1234',
    width = 300,
    height = 100
WHERE type = 'ALIMAMA';

-- 更新360广告的配置
UPDATE advertisement SET 
    qihoo_pos_id = 'qihu_12345',
    width = 300,
    height = 100
WHERE type = 'QIHOO';

-- 更新搜狗广告的配置
UPDATE advertisement SET 
    sogou_app_id = 'sogou_123456',
    width = 300,
    height = 100
WHERE type = 'SOGOU';

-- 更新京东联盟广告的配置
UPDATE advertisement SET 
    jd_union_id = 'jd_123456789',
    width = 300,
    height = 100
WHERE type = 'JD';

-- 更新拼多多推广的配置
UPDATE advertisement SET 
    pdd_app_key = 'pdd_123456789abcdef',
    width = 300,
    height = 100
WHERE type = 'PDD'; 