## 多数据库与多环境支持

本项目支持 MySQL、Supabase（PostgreSQL）、H2（测试）、多环境配置，所有配置文件集中在 `backend/config/env/` 目录。

### 环境与配置文件一览

| 环境名   | 配置文件                        | 数据库类型      | 用途说明                 |
|----------|----------------------------------|----------------|--------------------------|
| dev      | application-dev.yml              | MySQL          | 本地开发                 |
| test     | application-test.yml             | H2             | 自动化测试               |
| mysql    | application-mysql.yml            | MySQL          | 生产/测试（MySQL）       |
| postgres | application-postgres.yml         | PostgreSQL     | 生产/测试（PG/Supabase） |
| prod     | application-prod.yml             | PostgreSQL     | 生产推荐                 |

### 一键切换环境启动（推荐）

使用 PowerShell 脚本 `start-backend.ps1`，支持如下环境：
- dev（开发，默认MySQL）
- test（自动化测试，H2）
- mysql（MySQL）
- postgres（Supabase/PostgreSQL）
- prod（生产，PostgreSQL）

**用法示例：**
```powershell
# 启动开发环境
./start-backend.ps1 dev
# 启动测试环境
./start-backend.ps1 test
# 启动MySQL
./start-backend.ps1 mysql
# 启动Supabase/PostgreSQL
./start-backend.ps1 postgres
# 启动生产环境
./start-backend.ps1 prod
```

### 手动指定配置文件启动

```bash
java -jar backend.jar --spring.config.location=backend/config/env/application-mysql.yml --spring.profiles.active=mysql
```

请根据实际数据库连接信息修改对应配置文件。

## 敏感信息保护

为防止敏感信息泄露，建议将真实数据库密码等放在 `application-*.local.yml` 或 `.env` 文件中，主配置文件只保留模板和注释。

`.gitignore` 已自动忽略这些本地配置文件，团队成员可根据实际情况自行创建和维护。 