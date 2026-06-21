# JayBlog

前后端分离的个人博客系统，基于自研脚手架搭建。支持 Markdown 文章编辑、RBAC 权限管理、动态路由、访客统计等功能。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 前端框架 | Vue | 2.6.14 |
| UI 组件库 | Element UI | 2.15.14 |
| Markdown 编辑器 | mavon-editor | 2.10.4 |
| Markdown 渲染 | marked | 18.0.5 |
| 图表 | ECharts | 6.1.0 |
| HTTP 客户端 | axios | 1.17.0 |
| 路由 | Vue Router | 3.5.1 |
| 状态管理 | Vuex | 3.6.2 |
| 后端框架 | Spring Boot | 2.7.18 |
| 构建工具 | Maven | 多模块 |
| ORM | MyBatis-Plus | 3.5.3.1 |
| 数据库连接池 | Druid | 1.2.20 |
| 认证 | JWT (jjwt) | 0.9.1 |
| 工具库 | Hutool | 5.8.22 |
| JSON | fastjson / fastjson2 | 1.2.83 / 2.0.35 |
| HTTP 客户端 | OkHttp | 4.12.0 |
| 数据库 | MySQL | 8.0 |

## 目录结构

```
JayBlog/
├── db/                          # 数据库脚本
│   └── jayblog.sql              # 建表语句 + 初始数据
├── fileupload/                  # 用户上传封面图（运行时数据）
├── web-front/                   # 前端项目
│   ├── src/
│   │   ├── api/                 # 接口请求封装
│   │   ├── assets/              # 静态资源
│   │   ├── components/          # 通用组件
│   │   ├── layout/              # 后台布局组件
│   │   ├── router/              # 路由配置（含动态路由）
│   │   ├── store/               # Vuex 状态管理
│   │   ├── styles/              # 全局样式
│   │   ├── views/               # 页面视图
│   │   ├── App.vue
│   │   └── main.js
│   └── package.json
└── web-serve/                   # 后端项目（Maven 多模块）
    ├── entry/                   # 启动入口 + Spring Boot 配置
    ├── web/                     # Controller 层
    ├── service/                 # Service 业务逻辑层
    ├── dao/                     # 数据持久层 (MyBatis-Plus Mapper)
    ├── common/                  # 公共模块 (JWT, 工具类, 统一返回等)
    └── autogenerator/           # MyBatis-Plus 代码生成器
```

## 功能特性

- **文章管理**：Markdown 编辑与实时预览，支持草稿/发布/私密状态，封面图上传，浏览量统计，置顶功能
- **分类 & 标签**：文章归类与标签体系，按分类/标签筛选文章
- **评论系统**：文章评论管理
- **用户 & 角色**：RBAC 权限模型，管理员/普通用户分离
- **动态路由**：根据后端返回的菜单权限动态注册路由
- **访客统计**：前台文章浏览量统计
- **数据图表**：基于 ECharts 的后台数据可视化
- **前台博客**：首页、文章列表、文章详情、分类浏览、时间轴、关于页
- **前后端分离**：Vue history 模式 + Spring Boot REST API
- **JWT 认证**：登录签发 Token，axios 拦截器自动携带

## 快速开始

### 环境要求

| 依赖 | 版本 |
|------|------|
| Node.js | 21+ |
| JDK | 1.8 |
| MySQL | 8.0 |
| Maven | 3.6+ |

### 数据库初始化

1. 创建数据库：
   ```sql
   CREATE DATABASE myblog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
   ```
2. 导入建表脚本：
   ```bash
   mysql -u root -p myblog < db/jayblog.sql
   ```
3. 修改后端数据库连接配置（`web-serve/entry/src/main/resources/application.properties`）。

### 后端启动

```bash
cd web-serve

# 编译打包
mvn clean package -DskipTests

# 启动（默认端口 8080）
cd entry
mvn spring-boot:run
```

应用启动后访问 `http://localhost:8080`。

### 前端启动

```bash
cd web-front

# 安装依赖
npm install

# 开发模式运行（默认端口 8081）
npm run serve

# 生产构建
npm run build
```

开发环境默认代理后端 `http://localhost:8080`，可在 `web-front/.env.development` 中修改 `VUE_APP_BASE_API`。

### 访问

- 前台博客：`http://localhost:8081/front/index`
- 后台管理：`http://localhost:8081/login`