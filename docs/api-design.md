# 业主线上投票与自治系统 API 设计文档

## 1. API 概述

### 1.1 基础信息

- 基础URL: `/api/v1`
- 认证方式: JWT Bearer Token
- 内容类型: `application/json`
- 字符编码: UTF-8

### 1.2 统一响应格式

所有API响应均使用以下统一格式：

```json
{
  "code": 200,           // 业务状态码
  "message": "操作成功",  // 操作结果描述
  "data": {}             // 响应数据
}
```

### 1.3 错误码规范

| 状态码 | 描述 | 说明 |
|-------|------|-----|
| 200 | 成功 | 请求成功处理 |
| 400 | 请求错误 | 客户端请求参数错误 |
| 401 | 未授权 | 未登录或Token过期 |
| 403 | 禁止访问 | 无权限访问该资源 |
| 404 | 资源不存在 | 请求的资源不存在 |
| 500 | 服务器错误 | 服务器内部错误 |

## 2. 认证相关API

### 2.1 发送短信验证码

- **接口URL**: `/auth/sms-code`
- **请求方式**: POST
- **功能说明**: 发送手机验证码用于登录

#### 请求参数

```json
{
  "phone": "13800138000"  // 手机号
}
```

#### 响应参数

```json
{
  "code": 200,
  "message": "验证码发送成功",
  "data": {
    "expires_in": 300  // 验证码有效时间(秒)
  }
}
```

### 2.2 手机号验证码登录

- **接口URL**: `/auth/login`
- **请求方式**: POST
- **功能说明**: 使用手机号和验证码登录系统

#### 请求参数

```json
{
  "phone": "13800138000",  // 手机号
  "code": "123456"         // 验证码
}
```

#### 响应参数

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // JWT令牌
    "expires_in": 86400,                                // 令牌有效期(秒)
    "user_info": {
      "id": 10001,                                      // 用户ID
      "phone": "13800138000",                           // 手机号
      "name": "张三",                                    // 姓名(可能为空)
      "is_verified": false,                             // 是否已认证
      "status": "PENDING"                               // 状态: PENDING|APPROVED|REJECTED
    }
  }
}
```

### 2.3 管理员登录

- **接口URL**: `/auth/admin/login`
- **请求方式**: POST
- **功能说明**: 管理员账号密码登录

#### 请求参数

```json
{
  "username": "admin",      // 用户名
  "password": "password123" // 密码
}
```

#### 响应参数

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // JWT令牌
    "expires_in": 86400,                                // 令牌有效期(秒)
    "user_info": {
      "id": 1,                                          // 管理员ID
      "username": "admin",                              // 用户名
      "name": "系统管理员",                               // 姓名
      "role": "SYSTEM_ADMIN",                           // 角色
      "community_id": null                              // 社区ID(可能为空)
    }
  }
}
```

### 2.4 获取当前用户信息

- **接口URL**: `/auth/me`
- **请求方式**: GET
- **功能说明**: 获取当前登录用户的详细信息
- **权限要求**: 用户认证

#### 请求参数

无

#### 响应参数

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 10001,                       // 用户ID
    "phone": "13800138000",            // 手机号
    "name": "张三",                     // 姓名
    "id_card": "110101********0013",   // 身份证号(脱敏)
    "is_verified": true,               // 是否已认证
    "status": "APPROVED",              // 状态
    "created_at": "2023-06-01 10:00:00", // 注册时间
    "houses": [                           // 房产列表
      {
        "id": 20001,
        "community_id": 101,
        "community_name": "阳光小区",
        "building": "1",
        "unit": "2",
        "room": "301",
        "address": "北京市朝阳区阳光小区1栋2单元301室",
        "area": 120.5,
        "is_primary": true
      }
    ]
  }
}
```

## 3. 认证流程API

### 3.1 上传证件

- **接口URL**: `/verification/upload`
- **请求方式**: POST
- **功能说明**: 上传身份证或房产证照片
- **权限要求**: 用户认证
- **请求类型**: `multipart/form-data`

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|-----|
| file | File | 是 | 证件图片文件 |
| type | String | 是 | 证件类型，可选值: `ID_CARD`(身份证), `HOUSE_CERT`(房产证) |

#### 响应参数

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "file_id": 30001,             // 文件记录ID
    "file_url": "temp/url/path",  // 文件临时访问地址
    "ocr_result": {               // OCR识别结果(可能为null)
      "id_card": {                // 身份证识别结果
        "name": "张三",
        "id_number": "110101********0013",
        "address": "北京市朝阳区...",
        "valid_date": "20180101-20380101"
      }
    }
  }
}
```

### 3.2 提交认证申请

- **接口URL**: `/verification/submit`
- **请求方式**: POST
- **功能说明**: 提交认证申请进行审核
- **权限要求**: 用户认证

#### 请求参数

```json
{
  "id_card_file_id": 30001,       // 身份证文件ID
  "house_cert_file_id": 30002,    // 房产证文件ID
  "name": "张三",                  // 姓名
  "id_card": "110101********0013", // 身份证号
  "house_info": {
    "community_id": 101,           // 社区ID
    "building": "1",               // 楼栋号
    "unit": "2",                   // 单元号
    "room": "301",                 // 房间号
    "address": "北京市朝阳区阳光小区1栋2单元301室", // 完整地址
    "area": 120.5,                 // 面积
    "certificate_number": "京(2018)朝不动产权第0123456号" // 房产证号
  }
}
```

#### 响应参数

```json
{
  "code": 200,
  "message": "提交成功，等待审核",
  "data": {
    "verification_id": 40001,      // 认证申请ID
    "status": "PENDING",           // 状态
    "submit_time": "2023-06-01 10:30:00" // 提交时间
  }
}
```

### 3.3 查询认证状态

- **接口URL**: `/verification/status`
- **请求方式**: GET
- **功能说明**: 查询当前用户的认证状态
- **权限要求**: 用户认证

#### 请求参数

无

#### 响应参数

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "status": "PENDING",           // 认证状态: PENDING|APPROVED|REJECTED
    "submit_time": "2023-06-01 10:30:00", // 提交时间
    "review_time": null,           // 审核时间
    "reject_reason": null,         // 拒绝原因(如有)
    "reviewer": null               // 审核人(如有)
  }
}
``` 