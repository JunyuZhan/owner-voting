# 业主线上投票与自治系统 API 设计文档（续）

## 4. 投票相关API

### 4.1 获取投票列表

- **接口URL**: `/votes`
- **请求方式**: GET
- **功能说明**: 获取可参与的投票列表
- **权限要求**: 用户认证

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|-----|
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |
| status | String | 否 | 投票状态筛选，可选值: `PUBLISHED`(进行中), `ENDED`(已结束) |

#### 响应参数

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,               // 总记录数
    "pages": 10,                // 总页数
    "current": 1,               // 当前页码
    "size": 10,                 // 每页条数
    "records": [                // 投票列表
      {
        "id": 50001,            // 投票ID
        "title": "小区道路维修方案投票",  // 标题
        "description": "关于小区主干道维修的两种方案...",  // 描述
        "start_time": "2023-06-01 00:00:00",  // 开始时间
        "end_time": "2023-06-10 23:59:59",    // 结束时间
        "is_area_weighted": true,             // 是否按面积加权
        "is_real_name": true,                 // 是否实名投票
        "status": "PUBLISHED",                // 状态
        "is_voted": false,                    // 当前用户是否已投票
        "total_votes": 56,                    // 总投票数
        "options_count": 2                    // 选项数量
      }
    ]
  }
}
```

### 4.2 获取投票详情

- **接口URL**: `/votes/{id}`
- **请求方式**: GET
- **功能说明**: 获取指定投票的详细信息
- **权限要求**: 用户认证

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|-----|
| id | Integer | 是 | 投票ID |

#### 响应参数

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 50001,                         // 投票ID
    "title": "小区道路维修方案投票",        // 标题
    "description": "关于小区主干道维修的两种方案...",  // 描述
    "start_time": "2023-06-01 00:00:00", // 开始时间
    "end_time": "2023-06-10 23:59:59",   // 结束时间
    "is_area_weighted": true,            // 是否按面积加权
    "is_real_name": true,                // 是否实名投票
    "is_result_public": true,            // 结果是否公开
    "status": "PUBLISHED",               // 状态
    "created_by": "王物业",               // 创建人
    "created_at": "2023-05-28 15:30:00", // 创建时间
    "is_voted": false,                   // 当前用户是否已投票
    "user_vote": null,                   // 用户投票记录
    "options": [                         // 选项列表
      {
        "id": 60001,                     // 选项ID
        "option_text": "方案一：全部更换沥青路面",  // 选项内容
        "sort_order": 1                  // 排序
      },
      {
        "id": 60002,
        "option_text": "方案二：修补现有路面",
        "sort_order": 2
      }
    ],
    "result": {                          // 结果(如果允许查看)
      "total_votes": 56,                 // 总投票数
      "total_weight": 6890.5,            // 总权重
      "option_results": [
        {
          "option_id": 60001,
          "option_text": "方案一：全部更换沥青路面",
          "votes": 34,
          "weight": 4350.5,
          "percent": 63.14
        },
        {
          "option_id": 60002,
          "option_text": "方案二：修补现有路面",
          "votes": 22,
          "weight": 2540.0,
          "percent": 36.86
        }
      ]
    }
  }
}
```

### 4.3 提交投票

- **接口URL**: `/votes/{id}/cast`
- **请求方式**: POST
- **功能说明**: 对指定投票进行投票
- **权限要求**: 已认证用户

#### 请求参数

```json
{
  "option_id": 60001,     // 选择的选项ID
  "house_id": 20001       // 代表的房产ID
}
```

#### 响应参数

```json
{
  "code": 200,
  "message": "投票成功",
  "data": {
    "vote_id": 70001,     // 投票记录ID
    "vote_time": "2023-06-05 14:30:00", // 投票时间
    "vote_weight": 120.5, // 投票权重(面积或1)
    "topic_id": 50001,    // 议题ID
    "option_id": 60001    // 选项ID
  }
}
```

### 4.4 查看投票结果

- **接口URL**: `/votes/{id}/result`
- **请求方式**: GET
- **功能说明**: 查看指定投票的结果统计
- **权限要求**: 用户认证

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|-----|
| id | Integer | 是 | 投票ID |

#### 响应参数

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "topic_id": 50001,              // 议题ID
    "title": "小区道路维修方案投票",    // 标题
    "is_area_weighted": true,       // 是否按面积加权
    "total_votes": 56,              // 总投票数
    "total_weight": 6890.5,         // 总权重
    "option_results": [             // 选项结果
      {
        "option_id": 60001,
        "option_text": "方案一：全部更换沥青路面",
        "votes": 34,                // 票数
        "weight": 4350.5,           // 权重
        "percent": 63.14            // 百分比
      },
      {
        "option_id": 60002,
        "option_text": "方案二：修补现有路面",
        "votes": 22,
        "weight": 2540.0,
        "percent": 36.86
      }
    ],
    "vote_records": [               // 投票记录(是否显示视投票设置)
      {
        "house_address": "1栋2单元301室",
        "owner_name": "张三",
        "vote_time": "2023-06-05 14:30:00",
        "option_text": "方案一：全部更换沥青路面"
      }
    ]
  }
}
```

### 4.5 管理员创建投票

- **接口URL**: `/admin/votes`
- **请求方式**: POST
- **功能说明**: 创建新的投票议题
- **权限要求**: 管理员

#### 请求参数

```json
{
  "title": "小区道路维修方案投票",             // 标题
  "description": "关于小区主干道维修的两种方案...",  // 描述
  "start_time": "2023-06-01 00:00:00",      // 开始时间
  "end_time": "2023-06-10 23:59:59",        // 结束时间
  "is_area_weighted": true,                 // 是否按面积加权
  "is_real_name": true,                     // 是否实名投票
  "is_result_public": true,                 // 结果是否公开
  "community_id": 101,                      // 社区ID
  "options": [                              // 选项列表
    {
      "option_text": "方案一：全部更换沥青路面",  // 选项内容
      "sort_order": 1                        // 排序
    },
    {
      "option_text": "方案二：修补现有路面",
      "sort_order": 2
    }
  ]
}
```

#### 响应参数

```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 50001,                          // 投票ID
    "status": "DRAFT",                    // 状态
    "created_at": "2023-05-28 15:30:00"   // 创建时间
  }
}
```

## 5. 公告相关API

### 5.1 获取公告列表

- **接口URL**: `/announcements`
- **请求方式**: GET
- **功能说明**: 获取公告列表
- **权限要求**: 用户认证

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|-----|
| page | Integer | 否 | 页码，默认1 |
| size | Integer | 否 | 每页条数，默认10 |
| type | String | 否 | 公告类型，可选值: `NOTICE`, `VOTE_RESULT`, `FINANCIAL`, `OTHER` |

#### 响应参数

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 50,                // 总记录数
    "pages": 5,                 // 总页数
    "current": 1,               // 当前页码
    "size": 10,                 // 每页条数
    "records": [                // 公告列表
      {
        "id": 80001,            // 公告ID
        "title": "关于小区道路维修的通知",  // 标题
        "type": "NOTICE",       // 类型
        "is_pinned": true,      // 是否置顶
        "published_at": "2023-06-01 10:00:00",  // 发布时间
        "is_read": false,        // 当前用户是否已读
        "has_attachments": true  // 是否有附件
      }
    ]
  }
}
```

### 5.2 获取公告详情

- **接口URL**: `/announcements/{id}`
- **请求方式**: GET
- **功能说明**: 获取公告详情
- **权限要求**: 用户认证

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|-----|
| id | Integer | 是 | 公告ID |

#### 响应参数

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 80001,                        // 公告ID
    "title": "关于小区道路维修的通知",     // 标题
    "content": "经业主投票决定，将于6月15日开始对小区主干道进行维修...",  // 内容
    "type": "NOTICE",                   // 类型
    "is_pinned": true,                  // 是否置顶
    "published_at": "2023-06-01 10:00:00",  // 发布时间
    "published_by": "王物业",           // 发布人
    "attachments": [                    // 附件列表
      {
        "id": 90001,                    // 附件ID
        "original_name": "道路维修方案.pdf",  // 原始文件名
        "file_size": 1024000,           // 文件大小(字节)
        "file_type": "application/pdf", // 文件类型
        "download_url": "/api/v1/files/90001/download"  // 下载地址
      }
    ]
  }
}
```

### 5.3 标记公告已读

- **接口URL**: `/announcements/{id}/read`
- **请求方式**: POST
- **功能说明**: 标记公告为已读状态
- **权限要求**: 用户认证

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|-------|------|-----|-----|
| id | Integer | 是 | 公告ID |

#### 响应参数

```json
{
  "code": 200,
  "message": "标记成功",
  "data": {
    "announcement_id": 80001,      // 公告ID
    "read_at": "2023-06-05 16:30:00"  // 阅读时间
  }
}
``` 