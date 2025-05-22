-- 创建默认社区
INSERT INTO community (name, address, description, created_at) 
VALUES ('示范小区', '北京市海淀区中关村大街1号', '这是一个示范小区，用于系统演示', NOW());

-- 创建系统管理员账号
INSERT INTO admin_user (username, password_hash, name, role, enabled, created_at, updated_at) 
VALUES ('admin', '123456', '系统管理员', 'SYSTEM_ADMIN', TRUE, NOW(), NOW());

-- 创建社区管理员账号
INSERT INTO admin_user (username, password_hash, name, role, community_id, enabled, created_at, updated_at) 
VALUES ('community_admin', '123456', '社区管理员', 'COMMUNITY_ADMIN', 1, TRUE, NOW(), NOW());

-- 创建测试业主账号
INSERT INTO owner (phone, name, id_card, password_hash, is_verified, status, community_id, created_at, updated_at)
VALUES 
('13800000001', '张三', '110101199001011234', '$2a$10$tL6PD.GOlLAoOX8c5nNyPuJvwzP1xsj8BOzXDuWnMN0JnjY41gG7u', TRUE, 'APPROVED', 1, NOW(), NOW()),
('13800000002', '李四', '110101199001021234', '$2a$10$tL6PD.GOlLAoOX8c5nNyPuJvwzP1xsj8BOzXDuWnMN0JnjY41gG7u', TRUE, 'APPROVED', 1, NOW(), NOW()),
('13800000003', '王五', '110101199001031234', '$2a$10$tL6PD.GOlLAoOX8c5nNyPuJvwzP1xsj8BOzXDuWnMN0JnjY41gG7u', TRUE, 'APPROVED', 1, NOW(), NOW());

-- 创建房屋信息
INSERT INTO house (owner_id, community_id, building, unit, room, address, area, certificate_number, is_primary)
VALUES 
(1, 1, 'A栋', '1单元', '101', 'A栋1单元101', 98.5, 'CERT12345678', TRUE),
(1, 1, 'A栋', '1单元', '102', 'A栋1单元102', 120.3, 'CERT12345679', FALSE),
(2, 1, 'A栋', '2单元', '201', 'A栋2单元201', 89.7, 'CERT12345680', TRUE),
(3, 1, 'B栋', '1单元', '101', 'B栋1单元101', 110.5, 'CERT12345681', TRUE);

-- 创建示例投票主题
INSERT INTO vote_topic (community_id, title, description, start_time, end_time, is_area_weighted, is_real_name, is_result_public, status, created_by, created_at, updated_at)
VALUES 
(1, '关于小区大门口绿化改造方案的投票', '为了提升小区整体形象，拟对小区大门口进行绿化改造，现提供三种方案供业主投票选择。', 
 DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), TRUE, TRUE, TRUE, 'PUBLISHED', 'community_admin', NOW(), NOW()),
(1, '关于小区健身设施更新的投票', '小区现有健身设施使用已满5年，现计划更新，请业主投票选择喜欢的设施类型。', 
 DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), FALSE, TRUE, TRUE, 'PUBLISHED', 'community_admin', NOW(), NOW());

-- 创建投票选项
INSERT INTO vote_option (topic_id, option_text, order_num)
VALUES 
(1, '方案一：以乔木为主，配以花坛', 1),
(1, '方案二：以灌木为主，配以景观小品', 2),
(1, '方案三：以草坪为主，增加休闲座椅', 3),
(2, '更新跑步机和动感单车', 1),
(2, '更新户外健身器材', 2),
(2, '增加瑜伽区和拳击设备', 3);

-- 创建示例投票记录
INSERT INTO vote_record (topic_id, option_id, house_id, voter_id, vote_weight, vote_time, ip_address, device_info)
VALUES 
(1, 1, 1, 1, 98.5, DATE_SUB(NOW(), INTERVAL 9 DAY), '192.168.1.100', 'Chrome/Windows'),
(1, 2, 3, 2, 89.7, DATE_SUB(NOW(), INTERVAL 8 DAY), '192.168.1.101', 'Safari/MacOS'),
(1, 3, 4, 3, 110.5, DATE_SUB(NOW(), INTERVAL 7 DAY), '192.168.1.102', 'Firefox/Windows'),
(2, 4, 1, 1, 1.0, DATE_SUB(NOW(), INTERVAL 4 DAY), '192.168.1.100', 'Chrome/Windows'),
(2, 5, 3, 2, 1.0, DATE_SUB(NOW(), INTERVAL 3 DAY), '192.168.1.101', 'Safari/MacOS'),
(2, 6, 4, 3, 1.0, DATE_SUB(NOW(), INTERVAL 2 DAY), '192.168.1.102', 'Firefox/Windows');

-- 创建公告
INSERT INTO announcement (community_id, title, content, type, publisher_id, publish_time, created_at, updated_at)
VALUES 
(1, '小区物业管理规定通知', '尊敬的各位业主：\n为了维护小区良好的生活环境，现发布《物业管理规定》，请各位业主遵守。\n详细内容见附件。', 
 'NOTICE', 2, NOW(), NOW(), NOW()),
(1, '2023年第一季度投票结果公示', '尊敬的各位业主：\n2023年第一季度关于小区公共设施改造的投票已结束，现将结果公示如下：\n方案一：33票\n方案二：45票\n方案三：22票\n根据投票结果，将采用方案二进行实施。', 
 'VOTE_RESULT', 2, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY));

-- 创建公告阅读记录
INSERT INTO announcement_read (announcement_id, owner_id, read_time)
VALUES 
(1, 1, NOW()),
(1, 2, NOW()),
(2, 1, DATE_SUB(NOW(), INTERVAL 29 DAY));

-- 创建意见建议
INSERT INTO suggestion (community_id, owner_id, title, content, status, created_at, updated_at)
VALUES 
(1, 1, '关于增设电动车充电桩的建议', '随着小区电动车数量增加，现有充电设施已不能满足需求，建议在地下车库增设10个充电桩。', 'PENDING', NOW(), NOW()),
(1, 2, '关于加强小区安全巡逻的建议', '近期发现小区有外来人员随意进出，建议物业加强门禁管理和安全巡逻，保障小区安全。', 'PROCESSING', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 创建意见回复
INSERT INTO suggestion_reply (suggestion_id, replier_id, replier_type, content, reply_time)
VALUES 
(2, 2, 'ADMIN', '您好，我们已收到您的建议，将立即加强小区安保工作，增加巡逻频次，并对门禁系统进行升级。感谢您的宝贵建议！', DATE_SUB(NOW(), INTERVAL 4 DAY));

-- 创建系统日志
INSERT INTO system_log (user_id, user_type, operation, content, ip_address, created_time)
VALUES 
(1, 'ADMIN', 'LOGIN', '管理员登录系统', '192.168.1.100', NOW()),
(2, 'ADMIN', 'CREATE_VOTE', '创建投票主题：关于小区大门口绿化改造方案的投票', '192.168.1.101', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(1, 'OWNER', 'VOTE', '参与投票：关于小区大门口绿化改造方案的投票', '192.168.1.102', DATE_SUB(NOW(), INTERVAL 9 DAY)); 