-- 创建示例社区
INSERT INTO community (name, address, description, created_at) 
VALUES ('示范小区', '北京市海淀区中关村大街1号', '这是一个示范小区，用于系统演示', CURRENT_TIMESTAMP);

-- 创建系统管理员账号
INSERT INTO admin_user (username, password_hash, name, role, enabled, created_at, updated_at) 
VALUES ('admin', '123456', '系统管理员', 'SYSTEM_ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);