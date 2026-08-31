# JayBlog

前后端分离的个人博客系统，支持 Markdown 文章编辑、RBAC 权限管理、中英双语、暗色模式、PWA 离线访问、RSS 订阅等功能。

> **项目地址**：https://gitee.com/akaywj/jay.-blog
> **在线体验**：https://jayblog.buuicard.cn/

> 目录包含 **两代前端** 与 **一套后端**：
> - `JayBlogReBuild/` — **新版前端（Nuxt 4 + Vue 3），当前线上使用**
> - `web-front/` — 旧版前端（Vue 2 + Element UI），已被上述项目取代
> - `web-serve/` — 后端（Spring Boot 多模块）

---

## ✨ 项目总览

```
MyBlog/                 # 整个大项目
├── JayBlogReBuild/     # 新版前端（Nuxt 4 / SSR）—— 线上在用，见下方详述
├── web-front/          # 旧版前端（Vue 2 + Element UI）—— 已弃用
├── web-serve/          # 后端（Spring Boot 2.7 / MyBatis-Plus / MySQL）
├── db/                 # 数据库脚本（ms2026.sql / user.sql 等）
└── LICENSE
```

---

## 🚀 新版前端 `JayBlogReBuild/`（当前线上）

**Nuxt 4（SSR）+ Vue 3 + Element Plus + Pinia** 重构版，取代旧 Vue2 前端。

| 领域 | 技术 |
|------|------|
| 框架 | **Nuxt 4**（服务端渲染 SSR）+ **Vue 3**（Composition API） |
| UI | **Element Plus**（`@element-plus/nuxt`） |
| 状态 | **Pinia** + `pinia-plugin-persistedstate` |
| Markdown | `marked` + `dompurify`（XSS 白名单）+ `md-editor-v3`（后台编辑器） |
| 图表 | **ECharts** |
| 国际化 | **自研零依赖 `useI18n`**（`zh-CN` / `en-US`，Cookie + localStorage 持久化，SSR 一致） |
| 暗色 | **自研 `useDarkMode`** composable |
| PWA | **@vite-pwa/nuxt**（离线、安装到桌面、更新提示） |
| 分享 | `qrcode` + `jspdf` + `html2canvas`（二维码 / 复制链接 / PDF 封面） |
| HTTP | `$fetch` 自封装 + 全局拦截器（JWT 注入 / 401 处理） |

**功能特性**
- 前台：首页、文章阅读（**滚动高亮 TOC 目录**）、**文章分享**（二维码/复制链接/PDF 封面）、归档时间线、分类、关于、全文搜索、评论、点赞
- 后台：仪表盘（ECharts）、文章 / 分类 / 标签 / 评论 / 用户 / 角色 / 系统配置等 9 个管理页
- 全站：**中英双语**、**暗色模式**、**PWA 离线**、**RSS / sitemap**、**SEO**（OG / Twitter / JSON-LD）
- 部署：SSR（`.output/` + Node 进程 + nginx 反代）

> 详细文档见 [`JayBlogReBuild/README.md`](./JayBlogReBuild/README.md)

---

## 🗄 后端 `web-serve/`（Spring Boot 多模块）

| 模块 | 职责 |
|------|------|
| `entry` | 启动入口 + Spring Boot 配置 + 拦截器 |
| `web` | Controller 层 |
| `service` | Service 业务逻辑层 |
| `dao` | 数据持久层（MyBatis-Plus Mapper） |
| `common` | 公共模块（JWT、工具类、统一返回等） |

- 技术：Spring Boot 2.7 / MyBatis-Plus / MySQL 8 / JWT / Druid / Hutool
- 默认端口：**8086**
- 提供前台公开接口（文章/分类/评论/统计）+ 后台管理接口（含 RBAC 权限校验、验证码）

---

## 👴 旧版前端 `web-front/`（已弃用）

Vue 2.6 + Element UI + Vuex + axios 的**纯 SPA** 版本，已由 `JayBlogReBuild`（Nuxt 4 SSR）完全取代，仅作历史存档。

---

## 🚢 部署架构（线上）

```
用户 → nginx (443 SSL)
        ├── /api/*          → 后端 Spring Boot :8086
        ├── /upload/cover/* → 静态图片直托
        └── /               → Node (Nuxt SSR) :3000
```

新版前端为 **SSR**，需 Node 进程常驻；`/api` 由 nginx 反代到后端并剥离前缀。详细步骤见 `JayBlogReBuild/README.md#生产部署`。

---

## ⚙️ 快速启动（本地开发）

### 1. 后端 `web-serve/`
```bash
cd web-serve
mvn clean package -DskipTests
# 启动（默认端口 8086）
cd entry && mvn spring-boot:run
```

### 2. 新版前端 `JayBlogReBuild/`（当前线上）
```bash
cd JayBlogReBuild
npm install
npm run dev          # → http://localhost:3000
npm run build        # 生产构建 → .output/
```

### 3. 数据库
```bash
# 建库
CREATE DATABASE myblog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
# 导入脚本
mysql -u root -p myblog < db/ms2026.sql
```

---

## 📄 License
[MIT](./LICENSE)
