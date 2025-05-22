# 业主线上投票与自治系统开发文档

## 1. 项目概述

### 1.1 项目背景
本系统旨在为中国小区业主提供一个合法合规的线上投票与自治平台，解决传统业主大会组织困难、参与率低、决策不透明等问题。

### 1.2 系统目标
- 提供业主身份实名认证机制
- 实现高效透明的线上投票流程
- 建立业主与业委会/物业的沟通渠道
- 支持私有化部署，保护业主信息安全

### 1.3 用户角色
- 业主：系统主要用户，参与认证、投票和建议提交
- 小区管理员：负责业主认证审核、投票发起、公告管理
- 系统管理员：负责平台整体运维和权限管理

## 2. 技术架构

### 2.1 技术栈选型

| 层级 | 技术选择 | 版本 | 说明 |
|-----|---------|-----|-----|
| 前端 | Vue 3 | 3.2+ | 核心前端框架 |
|  | Element Plus | 2.2+ | UI组件库 |
|  | Pinia | 2.0+ | 状态管理 |
|  | Vue Router | 4.0+ | 路由管理 |
| 后端 | Spring Boot | 2.7+ | 后端框架 |
|  | Spring Security | 5.7+ | 安全框架 |
|  | Spring Data JPA | 2.7+ | ORM框架 |
|  | JWT | 0.11+ | 身份认证 |
| 数据库 | MySQL | 8.0+ | 关系型数据库 |
| 存储 | MinIO | 最新 | 对象存储服务 |
| OCR | PaddleOCR | 最新 | 文字识别 |
| 部署 | Docker | 最新 | 容器化部署 |
|  | Docker Compose | 最新 | 环境编排 |
|  | Nginx | 最新 | 反向代理 |

### 2.2 系统架构图

```
用户 -> HTTPS -> Nginx代理 -> 前端应用(Vue) <-> 后端API(Spring Boot) <-> MySQL
                                           |                |
                                           |                |-> MinIO(对象存储)
                                           |                |-> PaddleOCR(文字识别)
                                           |
                                           |-> 管理后台(Vue)
```

## 3. 环境搭建指南

### 3.1 开发环境要求
- JDK 11+
- Node.js 16+
- MySQL 8.0+
- Docker 20.10+
- Docker Compose 2.0+

### 3.2 本地开发环境搭建
```bash
# 后端项目初始化
mkdir -p owner-voting/backend
cd owner-voting/backend
# 使用Spring Initializr生成项目

# 前端项目初始化
cd ..
npm init vue@latest frontend
cd frontend
npm install element-plus pinia vue-router axios

# 数据库与依赖服务
cd ..
docker-compose -f docker-compose.dev.yml up -d
```

### 3.3 开发工具建议
- IDE：IntelliJ IDEA (后端)，VS Code (前端)
- API调试：Postman
- 数据库管理：DBeaver
- 版本控制：Git

## 4. 数据库设计

### 4.1 ER图
[此处应有ER图]

### 4.2 核心表设计

```sql
-- 用户表
CREATE TABLE owner (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  phone VARCHAR(20) NOT NULL UNIQUE,
  name VARCHAR(50),
  id_card VARCHAR(18),
  password_hash VARCHAR(255),
  is_verified BOOLEAN DEFAULT FALSE,
  status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL
);

-- 房产信息表
CREATE TABLE house (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  owner_id BIGINT,
  community_id BIGINT,
  building VARCHAR(50),
  unit VARCHAR(50),
  room VARCHAR(50),
  address VARCHAR(255),
  area DECIMAL(10,2),
  certificate_number VARCHAR(100),
  is_primary BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (owner_id) REFERENCES owner(id),
  FOREIGN KEY (community_id) REFERENCES community(id)
);

-- 社区/小区表
CREATE TABLE community (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  address VARCHAR(255),
  description TEXT,
  created_at DATETIME NOT NULL
);

-- 文件上传记录
CREATE TABLE file_upload (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  owner_id BIGINT,
  type ENUM('ID_CARD', 'HOUSE_CERT', 'OTHER') NOT NULL,
  original_name VARCHAR(255),
  storage_path TEXT NOT NULL,
  ocr_text TEXT,
  created_at DATETIME NOT NULL,
  FOREIGN KEY (owner_id) REFERENCES owner(id)
);

-- 投票议题表
CREATE TABLE vote_topic (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  community_id BIGINT,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  start_time DATETIME NOT NULL,
  end_time DATETIME NOT NULL,
  is_area_weighted BOOLEAN DEFAULT FALSE,
  is_real_name BOOLEAN DEFAULT TRUE,
  is_result_public BOOLEAN DEFAULT TRUE,
  status ENUM('DRAFT', 'PUBLISHED', 'ENDED', 'ARCHIVED') DEFAULT 'DRAFT',
  created_by BIGINT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (community_id) REFERENCES community(id),
  FOREIGN KEY (created_by) REFERENCES admin_user(id)
);

-- 投票选项表
CREATE TABLE vote_option (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  topic_id BIGINT NOT NULL,
  option_text VARCHAR(255) NOT NULL,
  sort_order INT DEFAULT 0,
  FOREIGN KEY (topic_id) REFERENCES vote_topic(id)
);

-- 投票记录表
CREATE TABLE vote_record (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  topic_id BIGINT NOT NULL,
  option_id BIGINT NOT NULL,
  house_id BIGINT NOT NULL,
  voter_id BIGINT NOT NULL,
  vote_weight DECIMAL(10,2) DEFAULT 1.0,
  vote_time DATETIME NOT NULL,
  ip_address VARCHAR(50),
  device_info VARCHAR(255),
  FOREIGN KEY (topic_id) REFERENCES vote_topic(id),
  FOREIGN KEY (option_id) REFERENCES vote_option(id),
  FOREIGN KEY (house_id) REFERENCES house(id),
  FOREIGN KEY (voter_id) REFERENCES owner(id)
);

-- 公告表
CREATE TABLE announcement (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  community_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content TEXT,
  type ENUM('NOTICE', 'VOTE_RESULT', 'FINANCIAL', 'OTHER') NOT NULL,
  is_pinned BOOLEAN DEFAULT FALSE,
  published_at DATETIME,
  created_by BIGINT,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (community_id) REFERENCES community(id),
  FOREIGN KEY (created_by) REFERENCES admin_user(id)
);

-- 公告附件表
CREATE TABLE announcement_attachment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  announcement_id BIGINT NOT NULL,
  original_name VARCHAR(255),
  storage_path TEXT NOT NULL,
  file_size BIGINT,
  file_type VARCHAR(50),
  created_at DATETIME NOT NULL,
  FOREIGN KEY (announcement_id) REFERENCES announcement(id)
);

-- 公告阅读记录
CREATE TABLE announcement_read (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  announcement_id BIGINT NOT NULL,
  owner_id BIGINT NOT NULL,
  read_at DATETIME NOT NULL,
  FOREIGN KEY (announcement_id) REFERENCES announcement(id),
  FOREIGN KEY (owner_id) REFERENCES owner(id)
);

-- 意见建议表
CREATE TABLE suggestion (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  community_id BIGINT NOT NULL,
  owner_id BIGINT,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  is_anonymous BOOLEAN DEFAULT FALSE,
  status ENUM('PENDING', 'PROCESSING', 'REPLIED', 'CONVERTED') DEFAULT 'PENDING',
  like_count INT DEFAULT 0,
  dislike_count INT DEFAULT 0,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (community_id) REFERENCES community(id),
  FOREIGN KEY (owner_id) REFERENCES owner(id)
);

-- 意见回复表
CREATE TABLE suggestion_reply (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  suggestion_id BIGINT NOT NULL,
  replier_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  created_at DATETIME NOT NULL,
  FOREIGN KEY (suggestion_id) REFERENCES suggestion(id),
  FOREIGN KEY (replier_id) REFERENCES admin_user(id)
);

-- 管理员用户表
CREATE TABLE admin_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  name VARCHAR(50),
  phone VARCHAR(20),
  email VARCHAR(100),
  role ENUM('SYSTEM_ADMIN', 'COMMUNITY_ADMIN', 'OPERATOR') NOT NULL,
  community_id BIGINT,
  is_active BOOLEAN DEFAULT TRUE,
  last_login_at DATETIME,
  created_at DATETIME NOT NULL,
  updated_at DATETIME NOT NULL,
  FOREIGN KEY (community_id) REFERENCES community(id)
);

-- 系统日志表
CREATE TABLE system_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  user_type ENUM('OWNER', 'ADMIN') NOT NULL,
  operation VARCHAR(100) NOT NULL,
  ip_address VARCHAR(50),
  detail TEXT,
  created_at DATETIME NOT NULL
);
```

### 4.3 索引设计

```sql
-- 用户表索引
CREATE INDEX idx_owner_phone ON owner(phone);
CREATE INDEX idx_owner_id_card ON owner(id_card);
CREATE INDEX idx_owner_status ON owner(status);

-- 房产表索引
CREATE INDEX idx_house_owner_id ON house(owner_id);
CREATE INDEX idx_house_community_id ON house(community_id);
CREATE INDEX idx_house_address ON house(community_id, building, unit, room);

-- 投票表索引
CREATE INDEX idx_vote_topic_community ON vote_topic(community_id, status);
CREATE INDEX idx_vote_topic_time ON vote_topic(start_time, end_time);
CREATE INDEX idx_vote_record_topic ON vote_record(topic_id);
CREATE INDEX idx_vote_record_house ON vote_record(house_id);
CREATE INDEX idx_vote_record_voter ON vote_record(voter_id);

-- 公告表索引
CREATE INDEX idx_announcement_community ON announcement(community_id, type);
CREATE INDEX idx_announcement_publish ON announcement(published_at DESC);
```

## 5. API设计

### 5.1 RESTful API规范
- 基础URL: `/api/v1`
- 身份认证: 请求头携带`Authorization: Bearer {token}`
- 响应格式:
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": {}
  }
  ```

### 5.2 核心API列表

#### 认证相关

| 方法 | 路径 | 描述 | 权限 |
|-----|------|-----|-----|
| POST | /auth/sms-code | 发送手机验证码 | 无需认证 |
| POST | /auth/login | 手机号+验证码登录 | 无需认证 |
| POST | /auth/admin/login | 管理员登录 | 无需认证 |
| GET | /auth/me | 获取当前用户信息 | 用户认证 |

#### 认证流程

| 方法 | 路径 | 描述 | 权限 |
|-----|------|-----|-----|
| POST | /verification/upload | 上传身份证/房产证 | 用户认证 |
| POST | /verification/submit | 提交认证申请 | 用户认证 |
| GET | /verification/status | 查询认证状态 | 用户认证 |

#### 投票管理

| 方法 | 路径 | 描述 | 权限 |
|-----|------|-----|-----|
| GET | /votes | 获取投票列表 | 用户认证 |
| GET | /votes/{id} | 获取投票详情 | 用户认证 |
| POST | /votes/{id}/cast | 提交投票 | 已认证用户 |
| GET | /votes/{id}/result | 查看投票结果 | 用户认证 |
| POST | /admin/votes | 创建投票 | 管理员 |
| PUT | /admin/votes/{id} | 更新投票 | 管理员 |
| DELETE | /admin/votes/{id} | 删除投票 | 管理员 |

#### 公告管理

| 方法 | 路径 | 描述 | 权限 |
|-----|------|-----|-----|
| GET | /announcements | 获取公告列表 | 用户认证 |
| GET | /announcements/{id} | 获取公告详情 | 用户认证 |
| POST | /announcements/{id}/read | 标记已读 | 用户认证 |
| POST | /admin/announcements | 创建公告 | 管理员 |
| PUT | /admin/announcements/{id} | 更新公告 | 管理员 |
| DELETE | /admin/announcements/{id} | 删除公告 | 管理员 |

#### 意见建议

| 方法 | 路径 | 描述 | 权限 |
|-----|------|-----|-----|
| GET | /suggestions | 获取建议列表 | 用户认证 |
| POST | /suggestions | 提交建议 | 用户认证 |
| POST | /suggestions/{id}/like | 点赞建议 | 用户认证 |
| GET | /admin/suggestions | 管理建议列表 | 管理员 |
| POST | /admin/suggestions/{id}/reply | 回复建议 | 管理员 |

### 5.3 API详细设计文档

[此处应包含各API的详细请求参数和响应格式]

## 6. 前端设计

### 6.1 页面路由设计

```javascript
const routes = [
  // 公共页面
  { path: '/', component: HomePage },
  { path: '/login', component: LoginPage },
  
  // 业主页面
  { 
    path: '/owner', 
    component: OwnerLayout,
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', component: OwnerDashboard },
      { path: 'verification', component: VerificationPage },
      { path: 'houses', component: MyHousesPage },
      { path: 'votes', component: VoteListPage },
      { path: 'votes/:id', component: VoteDetailPage },
      { path: 'announcements', component: AnnouncementListPage },
      { path: 'announcements/:id', component: AnnouncementDetailPage },
      { path: 'suggestions', component: SuggestionPage },
    ]
  },
  
  // 管理后台
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAdminAuth: true },
    children: [
      { path: 'dashboard', component: AdminDashboard },
      { path: 'owners', component: OwnerManagementPage },
      { path: 'verification', component: VerificationManagementPage },
      { path: 'votes', component: VoteManagementPage },
      { path: 'votes/create', component: VoteCreatePage },
      { path: 'votes/:id/edit', component: VoteEditPage },
      { path: 'announcements', component: AnnouncementManagementPage },
      { path: 'suggestions', component: SuggestionManagementPage },
      { path: 'system/users', component: SystemUserPage, meta: { requiresSysAdmin: true } },
      { path: 'system/logs', component: SystemLogPage, meta: { requiresSysAdmin: true } },
    ]
  }
]
```

### 6.2 状态管理设计

```javascript
// Pinia Store设计
const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    isVerified: false
  }),
  actions: {
    async login(phone, code) { /* ... */ },
    async fetchUserInfo() { /* ... */ },
    logout() { /* ... */ }
  }
})

const useVoteStore = defineStore('vote', {
  state: () => ({
    voteList: [],
    currentVote: null
  }),
  actions: {
    async fetchVotes() { /* ... */ },
    async fetchVoteDetail(id) { /* ... */ },
    async castVote(voteId, optionId) { /* ... */ }
  }
})

// 其他store: 公告、建议等
```

### 6.3 组件设计

#### 核心组件结构

```
- components/
  - common/
    - AppHeader.vue
    - AppFooter.vue
    - SideMenu.vue
    - Pagination.vue
    - FileUploader.vue
  - verification/
    - IdCardUpload.vue
    - HouseCertUpload.vue
    - OcrPreview.vue
  - votes/
    - VoteCard.vue
    - VoteForm.vue
    - VoteResult.vue
  - announcements/
    - AnnouncementCard.vue
    - AnnouncementForm.vue
  - suggestions/
    - SuggestionCard.vue
    - SuggestionForm.vue
```

## 7. 开发规范

### 7.1 代码规范

#### Java代码规范
- 遵循Google Java代码风格
- 类名使用PascalCase，方法和变量使用camelCase
- 常量使用UPPER_SNAKE_CASE
- 包名使用小写字母，使用反向域名命名

#### JavaScript/Vue代码规范
- 遵循Airbnb JavaScript风格指南
- Vue组件使用PascalCase命名
- 使用ESLint和Prettier进行代码格式化

### 7.2 项目结构规范

#### 后端项目结构
```
- src/main/java/com/ownervoting/
  - config/            # 配置类
  - controller/        # 控制器
  - service/           # 业务逻辑
  - repository/        # 数据访问
  - model/             # 数据模型
    - entity/          # 实体类
    - dto/             # 数据传输对象
    - vo/              # 视图对象
  - util/              # 工具类
  - exception/         # 异常处理
  - security/          # 安全相关
```

#### 前端项目结构
```
- src/
  - assets/            # 静态资源
  - components/        # 组件
  - views/             # 页面
  - router/            # 路由
  - stores/            # 状态管理
  - api/               # API调用
  - utils/             # 工具函数
  - styles/            # 样式文件
  - locales/           # 国际化
```

### 7.3 Git工作流规范
- 主分支: main, develop
- 功能分支: feature/xxx
- 修复分支: bugfix/xxx
- 发布分支: release/xxx
- 提交信息格式: `type(scope): subject`
  - 类型: feat, fix, docs, style, refactor, test, chore
  - 范围: 可选，指定改动模块
  - 主题: 简短描述改动内容

## 8. 测试计划

### 8.1 单元测试
- 后端: JUnit 5 + Mockito
- 前端: Jest + Vue Test Utils

### 8.2 集成测试
- API接口测试: REST Assured
- 数据库测试: TestContainers

### 8.3 端到端测试
- Cypress进行前端E2E测试

### 8.4 测试覆盖率目标
- 后端业务逻辑代码覆盖率 > 80%
- 前端组件测试覆盖率 > 70%

## 9. 部署指南

### 9.1 开发环境
```yaml
# docker-compose.dev.yml
version: '3'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: owner_voting
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      
  minio:
    image: minio/minio
    command: server /data --console-address ":9001"
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: minio
      MINIO_ROOT_PASSWORD: minio123
    volumes:
      - minio-data:/data
      
volumes:
  mysql-data:
  minio-data:
```

### 9.2 生产环境部署

```yaml
# docker-compose.yml
version: '3'
services:
  nginx:
    image: nginx:latest
    volumes:
      - ./nginx/conf:/etc/nginx/conf.d
      - ./nginx/ssl:/etc/nginx/ssl
      - ./frontend/dist:/usr/share/nginx/html
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - backend
      
  backend:
    build: ./backend
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/owner_voting
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      MINIO_ENDPOINT: http://minio:9000
      MINIO_ACCESS_KEY: ${MINIO_ACCESS_KEY}
      MINIO_SECRET_KEY: ${MINIO_SECRET_KEY}
    depends_on:
      - mysql
      - minio
      - paddleocr
      
  mysql:
    image: mysql:8.0
    volumes:
      - mysql-data:/var/lib/mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD}
      MYSQL_DATABASE: owner_voting
    restart: always
      
  minio:
    image: minio/minio
    command: server /data
    volumes:
      - minio-data:/data
    environment:
      MINIO_ROOT_USER: ${MINIO_ACCESS_KEY}
      MINIO_ROOT_PASSWORD: ${MINIO_SECRET_KEY}
    restart: always
      
  paddleocr:
    build: ./paddleocr
    restart: always
    
volumes:
  mysql-data:
  minio-data:
```

### 9.3 部署流程
1. 前端构建
   ```bash
   cd frontend
   npm install
   npm run build
   ```

2. 后端打包
   ```bash
   cd backend
   ./mvnw clean package -DskipTests
   ```

3. 环境变量配置
   创建`.env`文件，设置敏感配置参数

4. 启动服务
   ```bash
   docker-compose up -d
   ```

5. 数据库初始化
   ```bash
   docker-compose exec mysql mysql -uroot -p{password} owner_voting < init.sql
   ```

6. 配置MinIO
   ```bash
   docker-compose exec minio mc config host add myminio http://localhost:9000 ${MINIO_ACCESS_KEY} ${MINIO_SECRET_KEY}
   docker-compose exec minio mc mb myminio/owner-voting
   ```

## 10. 安全考虑

### 10.1 数据安全
- 身份证、房产证等敏感信息加密存储
- MinIO对象存储访问控制
- 数据库定期备份

### 10.2 访问安全
- JWT Token安全策略
- Spring Security权限控制
- 敏感操作日志记录
- HTTPS加密传输

### 10.3 业务安全
- 投票流程防篡改机制
- 房产验证多重校验
- IP与设备异常访问检测

## 11. 后续规划与扩展

### 11.1 功能扩展
- 微信小程序版本
- 短信通知系统
- 人脸识别辅助验证
- 投票报表与分析功能

### 11.2 技术演进
- 微服务架构转型
- 实时消息推送
- 大数据分析支持

## 12. 附录

### 12.1 参考文档
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Vue 3官方文档](https://v3.vuejs.org/)
- [Element Plus组件库](https://element-plus.org/)
- [PaddleOCR文档](https://github.com/PaddlePaddle/PaddleOCR)
- [MinIO文档](https://min.io/docs/minio/linux/index.html)

### 12.2 术语表
- 业主：拥有小区内房产的个人或组织
- 业委会：业主委员会，由业主选举产生的自治组织
- 业主大会：由全体业主组成的议事决策机构
- 认证：验证业主身份的过程
- 投票权重：按照房屋面积或户数计算的投票比例 