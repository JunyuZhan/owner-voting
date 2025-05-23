# 生产环境部署指南

## 目录

1. [环境准备](#1-环境准备)
2. [传统部署流程](#2-传统部署流程)
   - [后端部署](#后端部署)
   - [前端部署](#前端部署)
   - [Nginx 代理配置](#nginx-代理配置)
   - [数据库初始化与迁移](#数据库初始化与迁移)
3. [Docker Compose 一键部署](#3-docker-compose-一键部署可选)
4. [CI/CD 自动化建议](#4-cicd-自动化建议)
5. [常见问题与排查](#5-常见问题与排查)
6. [云原生与高可用部署（进阶）](#6-云原生与高可用部署（进阶）)
   - [6.1 Kubernetes（K8s）部署建议](#61-kubernetes（k8s）部署建议)
   - [6.2 监控与告警](#62-监控与告警)
   - [6.3 日志收集与分析](#63-日志收集与分析)
   - [6.4 数据备份与恢复](#64-数据备份与恢复)
7. [安全加固建议](#7-安全加固建议)
8. [生产环境运维建议](#8-生产环境运维建议)

---

## 1. 环境准备

- **JDK 11+**（建议 OpenJDK 17）
- **Node.js 16+**（仅前端构建时需要）
- **Nginx 1.18+**
- **MySQL 8+ 或 PostgreSQL 13+**

---

## 2. 传统部署流程

### 后端部署

1. **构建后端 jar 包**
   ```bash
   cd backend
   ./mvnw clean package -DskipTests
   # 生成 target/backend.jar
   ```
2. **准备配置文件**
   - 将生产环境配置文件（如 `application-prod.yml`）放到服务器指定目录
   - 建议将真实密码等敏感信息写在 `application-prod.local.yml`，并在启动时通过 `--spring.config.location` 指定
3. **启动后端服务**
   ```bash
   java -jar backend.jar --spring.config.location=backend/config/env/application-prod.yml --spring.profiles.active=prod
   ```

### 前端部署

1. **构建前端静态资源**
   ```bash
   cd frontend
   npm install
   npm run build
   # 生成 dist 目录
   ```
2. **将 dist 目录内容上传到服务器 Nginx 静态资源目录**
   - 如 `/usr/share/nginx/html`

### Nginx 代理配置

参考典型配置：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

- 前端构建产物放在 `/usr/share/nginx/html`
- 所有 `/api/` 请求转发到后端 Spring Boot 服务
- 生产环境建议将 `VITE_API_BASE_URL` 配置为 `/api`

### 数据库初始化与迁移

- 系统采用 Flyway 自动管理数据库结构和数据迁移
- 首次启动后端服务时会自动初始化数据库结构和基础数据
- 如需手动迁移，可执行：
  ```bash
  java -jar backend.jar -Dflyway.migrate
  ```

---

## 3. Docker Compose 一键部署（可选）

推荐使用 Docker Compose 管理前后端、数据库、Nginx 服务，简化运维。

### 示例 docker-compose.yml

```yaml
version: '3.8'
services:
  db:
    image: mysql:8.0
    container_name: voting-mysql
    environment:
      MYSQL_ROOT_PASSWORD: yourpassword
      MYSQL_DATABASE: owner_voting
    ports:
      - "3306:3306"
    volumes:
      - ./mysql-data:/var/lib/mysql
    command: --default-authentication-plugin=mysql_native_password

  backend:
    image: openjdk:17-jdk
    container_name: voting-backend
    working_dir: /app
    volumes:
      - ./backend/target/backend.jar:/app/backend.jar
      - ./backend/config/env/application-prod.yml:/app/application-prod.yml
    command: ["java", "-jar", "backend.jar", "--spring.config.location=/app/application-prod.yml", "--spring.profiles.active=prod"]
    ports:
      - "8080:8080"
    depends_on:
      - db

  frontend:
    image: node:18
    container_name: voting-frontend
    working_dir: /app
    volumes:
      - ./frontend:/app
    command: ["npm", "run", "build"]
    environment:
      - VITE_API_BASE_URL=/api
    depends_on:
      - backend

  nginx:
    image: nginx:1.24
    container_name: voting-nginx
    ports:
      - "80:80"
    volumes:
      - ./frontend/dist:/usr/share/nginx/html
      - ./nginx.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - frontend
      - backend
```

> 你需要准备好 `nginx.conf`，内容可参考上文 Nginx 配置。

### 启动命令

```bash
docker-compose up -d
```

---

## 4. CI/CD 自动化建议

- 推荐使用 GitHub Actions、GitLab CI、Jenkins 等工具实现自动化构建与部署。
- 典型流程：
  1. 代码 push 后自动运行单元测试
  2. 自动构建前端和后端产物
  3. 自动推送 Docker 镜像到镜像仓库
  4. 自动部署到服务器（可用 SSH、K8s、云服务等）

---

## 5. 常见问题与排查

- **前端页面空白/404**：检查 Nginx 静态资源目录和 `try_files` 配置
- **API 403/401**：检查后端安全配置、Token 传递、跨域设置
- **数据库连接失败**：检查数据库地址、端口、用户名、密码、网络防火墙
- **端口冲突**：确保 80（Nginx）、8080（后端）未被占用
- **静态资源缓存问题**：可在 Nginx 配置中添加 `add_header Cache-Control no-cache;`

---

## 6. 云原生与高可用部署（进阶）

### 6.1 Kubernetes（K8s）部署建议

- 提供 `Deployment`、`Service`、`Ingress` 等 YAML 模板，支持自动扩缩容、滚动升级
- 可配合云厂商（如阿里云ACK、腾讯云TKE、AWS EKS）实现弹性伸缩
- 示例：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: voting-backend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: voting-backend
  template:
    metadata:
      labels:
        app: voting-backend
    spec:
      containers:
        - name: backend
          image: your-backend-image:latest
          ports:
            - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: voting-backend
spec:
  selector:
    app: voting-backend
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
```

### 6.2 监控与告警

- 推荐集成 Prometheus + Grafana 监控后端、数据库、Nginx
- 前端可用 Sentry 监控 JS 错误
- 配置邮件、钉钉、企业微信等告警通道

### 6.3 日志收集与分析

- 后端日志建议输出到文件，配合 ELK（Elasticsearch + Logstash + Kibana）或 Loki + Grafana 实现日志检索
- Nginx、数据库日志也可统一收集

### 6.4 数据备份与恢复

- 定期自动备份数据库（MySQL/PostgreSQL），可用脚本或云厂商服务
- 重要配置文件、用户上传数据建议定期快照

---

## 7. 安全加固建议

- 强制 HTTPS，Nginx 配置 SSL 证书
- 后端开启 CSRF/XSS 防护
- 数据库账号权限最小化
- 定期升级依赖，修复安全漏洞

---

## 8. 生产环境运维建议

- 配置健康检查（Nginx、Spring Boot actuator）
- 生产环境关闭调试、日志级别降为 WARN
- 重要操作加审计日志

---

如需更多云部署、监控、备份等脚本和模板，请联系项目维护者。 