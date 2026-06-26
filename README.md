<div align="center">


# 🍳 DishMind · 食谱推荐系统

**基于用户行为推荐的个性化菜谱微信小程序**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.6.13-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-1.8-orange.svg)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-5.7+-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-5.0+-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## 📑 目录

- [项目简介](#-项目简介)
- [功能特性](#-功能特性)
- [技术栈](#-技术栈)
- [项目结构](#-项目结构)
- [本地快速启动](#-本地快速启动)
- [部署文档](#-部署文档)
- [API 接口说明](#-api-接口说明)
- [数据库设计说明](#-数据库设计说明)
- [推荐算法说明](#-推荐算法说明)
- [贡献指南](#-贡献指南)
- [开源协议](#-开源协议)
- [作者与联系](#-作者与联系)

---

## 🍽 项目简介

**DishMind** 是一款面向烹饪爱好者、厨房小白、大学生及家庭主妇 / 主夫的个性化菜谱推荐小程序。项目以“菜谱 + 推荐”为核心，通过采集用户的浏览、收藏、点击、不喜欢等行为数据，构建用户标签偏好画像，并基于协同过滤思想实现“猜你喜欢”的个性化推荐。

无论你是想做一道快手晚餐，还是学习一道宴客硬菜，DishMind 都能根据你的口味和使用习惯，推荐最合适的菜谱。

> 🚧 项目状态：练手项目开发中，欢迎学习交流。

---

## ✨ 功能特性

### 用户端

| 模块        | 功能说明                                                 |
| ----------- | -------------------------------------------------------- |
| 🏠 首页推荐  | 热门菜谱、最新菜谱、为你推荐（个性化推荐）               |
| 🔍 搜索发现  | 支持按菜谱名称关键词搜索，支持多标签组合筛选             |
| 📖 菜谱详情  | 查看菜谱封面、简介、难度、耗时、评分，以及分步骤烹饪引导 |
| 👨‍🍳 做饭模式 | 菜谱步骤拆解，图文结合，按部就班跟着做                   |
| ❤️ 收藏夹    | 支持收藏 / 取消收藏，支持按文件夹分类管理收藏            |
| 👤 个人中心  | 用户信息管理、我的菜谱、我的收藏                         |
| 📝 发布菜谱  | 用户可创建菜谱、添加步骤、选择标签，提交后进入审核流程   |

### 管理端

| 模块       | 功能说明                                                   |
| ---------- | ---------------------------------------------------------- |
| ✅ 菜谱审核 | 审核用户提交的菜谱：通过（变为正常显示）或驳回（退回草稿） |
| 👥 用户管理 | 查看和管理注册用户                                         |
| 🏷️ 标签管理 | 管理标签及标签分类                                         |

### 核心亮点

- **个性化推荐**：基于用户历史行为计算标签偏好分数，结合标签置信度为每个用户生成“猜你喜欢”列表。
- **行为缓冲池**：用户行为数据先写入 Redis 缓冲，定时批量落库，降低数据库写入压力。
- **多标签组合筛选**：支持按口味、菜系、食材、场景、难度等维度组合筛选菜谱。
- **菜谱状态机**：草稿 → 审核中 → 正常显示 → 下架，支持重新发布。
- **分步骤引导**：菜谱支持多步骤图文说明，每步可包含预计耗时和小贴士。

---

## 🛠 技术栈

### 后端

| 技术               | 说明                             |
| ------------------ | -------------------------------- |
| Spring Boot 2.6.13 | 主框架                           |
| Spring Data JPA    | ORM 持久层                       |
| MySQL 5.7+         | 业务数据库                       |
| Redis              | 用户行为缓冲、偏好计算缓存       |
| JWT                | 用户认证（已引入，预留接口扩展） |
| Maven              | 项目构建工具                     |
| Lombok             | 简化实体类代码                   |

### 前端

| 技术               | 说明             |
| ------------------ | ---------------- |
| 微信小程序原生框架 | WXML + WXSS + JS |
| 微信开发者工具     | 开发调试         |

### 开发环境

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+
- 微信开发者工具

---

## 📁 项目结构

```
dishmind/
├── backend/                          # 后端服务（Spring Boot）
│   ├── src/main/java/com/bdu/dishmind/
│   │   ├── config/                   # 配置类（CORS、行为缓冲配置）
│   │   ├── controller/               # REST API 控制器
│   │   ├── dto/                      # 请求 / 响应数据传输对象
│   │   ├── entity/                   # JPA 实体类
│   │   ├── repository/               # Spring Data JPA 数据访问层
│   │   ├── scheduler/                # 定时任务（行为数据刷盘）
│   │   ├── service/                  # 业务逻辑层
│   │   │   ├── impl/                 # 业务实现
│   │   │   └── buffer/               # 行为缓冲服务
│   │   ├── shutdown/                 # 应用关闭钩子
│   │   └── utils/                    # 工具类、全局异常处理
│   ├── src/main/resources/
│   │   └── application-test.yml      # 测试环境配置（H2 + Redis）
│   └── pom.xml                       # Maven 依赖配置
├── db/
│   └── dishmind.sql                  # MySQL 初始化脚本（含测试数据）
└── frontend/                         # 微信小程序前端
    ├── app.js / app.json / app.wxss  # 小程序全局配置
    ├── pages/                        # 页面
    │   ├── index/                    # 首页
    │   ├── category/                 # 分类 / 发现页
    │   ├── search/                   # 搜索页
    │   ├── recipe/                   # 菜谱详情、烹饪、编辑
    │   ├── user/                     # 个人中心、收藏、我的菜谱
    │   └── login/                    # 登录页
    ├── components/                   # 公共组件
    │   ├── recipe-card/              # 菜谱卡片
    │   ├── step-item/                # 步骤项
    │   ├── tag-pill/                 # 标签 pill
    │   └── loading-more/             # 加载更多
    └── utils/                        # 前端工具类
        ├── request.js                # 统一网络请求封装
        ├── util.js                   # 通用工具函数
        └── behavior.js               # 行为上报工具
```

---

## 🚀 本地快速启动

### 1. 环境准备

确保本地已安装：

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+
- 微信开发者工具

### 2. 初始化数据库

```bash
# 登录 MySQL 并创建数据库
mysql -u root -p
```

```sql
CREATE DATABASE dishmind CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE dishmind;
SOURCE db/dishmind.sql;
```

### 3. 启动后端服务

在项目根目录下创建 `backend/src/main/resources/application.yml`（已加入 `.gitignore`），参考配置如下：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dishmind?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
  redis:
    host: localhost
    port: 6379
    database: 0

behavior:
  buffer:
    enabled: true
    flush-interval-ms: 5000
    batch-size: 100
```

然后编译并运行：

```bash
cd backend
mvn clean package -DskipTests
mvn spring-boot:run
```

服务启动后，访问 `http://localhost:8080/api` 即可。

### 4. 启动前端小程序

1. 打开微信开发者工具。
2. 选择“导入项目”，目录选择 `frontend/`。
3. 修改 `frontend/utils/request.js` 中的 `BASE_URL` 为你本地或已部署的后端地址：

```js
const BASE_URL = 'http://localhost:8080/api';
```

4. 点击“编译”，即可在模拟器中预览。

### 5. 快速体验账号

数据库初始化脚本中已包含测试账号：

| 账号  | 密码  | 角色     |
| ----- | ----- | -------- |
| 1     | 1     | 普通用户 |
| 2     | 2     | 普通用户 |
| admin | admin | 管理员   |

---

## 📦 部署文档

### 后端部署

#### 方式一：Jar 包部署

```bash
cd backend
mvn clean package -DskipTests
java -jar target/dishmind-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:mysql://<your-mysql-host>:3306/dishmind \
  --spring.datasource.username=<username> \
  --spring.datasource.password=<password> \
  --spring.redis.host=<your-redis-host>
```

#### 方式二：使用测试配置快速验证

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

> 测试配置使用 H2 内存数据库，仅用于本地快速验证接口，**不适合生产环境**。

### 前端部署

1. 将 `frontend/utils/request.js` 中的 `BASE_URL` 修改为生产环境域名：

```js
const BASE_URL = 'https://your-domain.com/api';
```

2. 在微信开发者工具中点击“上传”，填写版本号和项目备注。
3. 登录[微信公众平台](https://mp.weixin.qq.com/)，在“版本管理”中提交审核并发布。

> 小程序请求域名需要在微信公众平台「开发 → 开发管理 → 服务器域名」中进行配置。

---

## 🔌 API 接口说明

### 通用响应格式

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

### 用户模块 `/api/user`

| 请求方式 | 接口                                | 说明         |
| -------- | ----------------------------------- | ------------ |
| POST     | `/api/user/register`                | 用户注册     |
| POST     | `/api/user/login`                   | 用户登录     |
| GET      | `/api/user/profile?userId={userId}` | 获取用户信息 |
| POST     | `/api/user/profile/update`          | 更新用户昵称 |

### 菜谱模块 `/api/recipe`

| 请求方式 | 接口                                                        | 说明                       |
| -------- | ----------------------------------------------------------- | -------------------------- |
| GET      | `/api/recipe/list`                                          | 菜谱列表（默认按热度排序） |
| GET      | `/api/recipe/my?userId={userId}`                            | 我发布的菜谱               |
| GET      | `/api/recipe/search`                                        | 按标题关键词搜索           |
| GET      | `/api/recipe/filter?tagIds=1,2`                             | 多标签组合筛选             |
| GET      | `/api/recipe/{id}?userId={userId}`                          | 菜谱详情                   |
| GET      | `/api/recipe/{id}/similar`                                  | 相似菜谱推荐               |
| GET      | `/api/recipe/{id}/steps`                                    | 获取菜谱步骤               |
| POST     | `/api/recipe/create`                                        | 创建菜谱                   |
| POST     | `/api/recipe/{recipeId}/steps`                              | 批量保存菜谱步骤           |
| POST     | `/api/recipe/{recipeId}/steps/{stepId}/update`              | 更新单步骤                 |
| POST     | `/api/recipe/{recipeId}/steps/{stepId}/delete`              | 删除单步骤（软删除）       |
| POST     | `/api/recipe/{recipeId}/steps/{stepId}/restore`             | 恢复已删除步骤             |
| POST     | `/api/recipe/{id}/republish`                                | 重新发布菜谱               |
| POST     | `/api/recipe/{id}/offline`                                  | 下架菜谱                   |
| POST     | `/api/recipe/{id}/approve?adminId={adminId}`                | 管理员审核通过             |
| POST     | `/api/recipe/{id}/reject?adminId={adminId}&reason={reason}` | 管理员审核驳回             |
| POST     | `/api/recipe/{id}/dislike?userId={userId}`                  | 不喜欢该菜谱               |

### 推荐模块 `/api/recommend`

| 请求方式 | 接口                                     | 说明               |
| -------- | ---------------------------------------- | ------------------ |
| GET      | `/api/recommend/for-you?userId={userId}` | 为你推荐（个性化） |
| GET      | `/api/recommend/hot`                     | 热门菜谱           |
| GET      | `/api/recommend/latest`                  | 最新菜谱           |

### 收藏模块 `/api/favorite`

| 请求方式 | 接口                           | 说明                 |
| -------- | ------------------------------ | -------------------- |
| POST     | `/api/favorite/toggle`         | 收藏 / 取消收藏      |
| GET      | `/api/favorite/check`          | 检查是否已收藏       |
| GET      | `/api/favorite/list`           | 我的收藏列表         |
| GET      | `/api/favorite/folders`        | 收藏文件夹列表       |
| POST     | `/api/favorite/folder/rename`  | 重命名收藏文件夹     |
| POST     | `/api/favorite/folder/delete`  | 删除收藏文件夹       |
| GET      | `/api/favorite/list-by-folder` | 按文件夹查看收藏     |
| POST     | `/api/favorite/foldermove`     | 移动收藏到指定文件夹 |

### 标签模块 `/api/tag`

| 请求方式 | 接口                             | 说明                   |
| -------- | -------------------------------- | ---------------------- |
| GET      | `/api/tag/all`                   | 获取所有标签分类及标签 |
| GET      | `/api/tag/category/{categoryId}` | 按分类获取标签         |
| POST     | `/api/tag/dislike`               | 不喜欢某个标签         |

### 行为模块 `/api/behavior`

| 请求方式 | 接口                   | 说明                                                     |
| -------- | ---------------------- | -------------------------------------------------------- |
| POST     | `/api/behavior/report` | 上报用户行为（VIEW / CLICK / COLLECT / SHARE / DISLIKE） |

---

## 🗄 数据库设计说明

本项目采用 MySQL 关系型数据库，主要包含以下核心表：

| 表名                  | 说明                                                     |
| --------------------- | -------------------------------------------------------- |
| `user`                | 用户信息，支持普通用户与管理员                           |
| `recipe`              | 菜谱主表，包含标题、简介、封面、难度、耗时、评分、状态等 |
| `recipe_step`         | 菜谱步骤表，支持步骤序号、描述、配图、小贴士、预计耗时   |
| `tag_category`        | 标签分类表（口味、菜系、食材、场景、难度、功效）         |
| `tag`                 | 标签表，归属某一分类                                     |
| `recipe_tag`          | 菜谱与标签的关联表，支持标签权重                         |
| `user_favorite`       | 用户收藏表，支持文件夹分类                               |
| `user_behavior`       | 用户行为记录表（浏览、点击、收藏、分享、不喜欢）         |
| `user_tag_preference` | 用户标签偏好表，用于推荐计算                             |

### 菜谱状态说明

| 状态值 | 含义     |
| ------ | -------- |
| 0      | 草稿     |
| 1      | 审核中   |
| 2      | 正常显示 |
| 3      | 已下架   |

### 用户行为类型

| 行为类型  | 说明              |
| --------- | ----------------- |
| VIEW      | 浏览菜谱          |
| CLICK     | 点击菜谱详情      |
| COLLECT   | 收藏菜谱          |
| UNCOLLECT | 取消收藏          |
| SHARE     | 分享菜谱          |
| DISLIKE   | 不喜欢菜谱 / 标签 |

---

## 🎯 推荐算法说明

DishMind 的“为你推荐”采用基于用户标签偏好的推荐策略，核心流程如下：

1. **行为采集**：前端上报用户的浏览、收藏、点击、不喜欢等行为，后端先写入 Redis 缓冲池。
2. **定时落盘**：通过定时任务将 Redis 中的行为数据批量写入 `user_behavior` 表，并更新 `user_tag_preference`。
3. **偏好计算**：根据用户历史行为，计算每个标签的得分、正反馈次数、负反馈次数。
4. **推荐生成**：
   - 获取用户 Top 5 偏好标签；
   - 计算每个标签的置信度：`positive / (positive + negative + 1)`；
   - 查询包含这些标签的候选菜谱，排除已浏览过的菜谱；
   - 按 `∑(recipe_tag.weight × 标签置信度)` 重新排序，返回 Top N 推荐结果。
5. **冷启动兜底**：新用户或偏好为空时，返回热门菜谱作为兜底。

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库。
2. 创建你的特性分支：`git checkout -b feature/YourFeature`。
3. 提交你的改动：`git commit -m 'Add some feature'`。
4. 推送到分支：`git push origin feature/YourFeature`。
5. 创建 Pull Request。

提交前请确保：

- 代码风格与现有项目保持一致。
- 新增的接口或功能补充必要的说明。
- 不要提交敏感配置（如数据库密码、小程序 AppID 等）。

---

## 📄 开源协议

本项目采用 [MIT](LICENSE) 协议开源。

---

## 👨‍💻 作者与联系

- **作者**：zlzpic
- **邮箱**：zlizz2198@qq.com
- **GitHub**：[https://github.com/zlzpic](https://github.com/zlzpic)

如果你对这个项目感兴趣，欢迎 Star ⭐ 和 Fork 🍴，有任何问题也可以通过邮箱联系我。

---

> 本项目为个人练手项目，代码和文档会持续完善，感谢你的关注与支持！
