-- MySQL dump 10.13  Distrib 8.0.39, for macos14 (arm64)
--
-- Host: localhost    Database: myblog
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '发布人ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `tag_ids` varchar(255) DEFAULT '' COMMENT '标签ID，多个用逗号分隔',
  `title` varchar(200) NOT NULL COMMENT '文章标题',
  `summary` varchar(500) DEFAULT '' COMMENT '文章摘要',
  `cover` varchar(255) DEFAULT '' COMMENT '封面图',
  `content_md` longtext NOT NULL COMMENT 'markdown原文',
  `content_html` longtext NOT NULL COMMENT '渲染后html',
  `view_count` bigint DEFAULT '0' COMMENT '浏览量',
  `like_count` bigint DEFAULT '0' COMMENT '点赞数',
  `is_top` tinyint DEFAULT '0' COMMENT '0不置顶 1置顶',
  `status` tinyint NOT NULL COMMENT '1草稿 2已发布 3私密',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='博客文章';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (1,9,1,'','测试','测试','/upload/cover/20260620/d8ee2e697c3a4930bc911f2ad8889a41.png','## 测试\n### 1\n- 1\n- 2\n- 3','<h2><a id=\"_0\"></a>测试</h2>\n<h3><a id=\"1_1\"></a>1</h3>\n<ul>\n<li>1</li>\n<li>2</li>\n<li>3</li>\n</ul>\n',47,3,0,2,'2026-06-20 03:01:54','2026-06-21 04:26:21',1),(2,9,1,'2,1','JayBlog 使用指南','JayBlog 使用指南','/upload/cover/20260620/1db8108f4eba44b195b7107f2c392b4a.jpg','# JayBlog 使用指南\n\nJayBlog 是一套前后端分离的个人博客系统，分为**前台展示**和**后台管理**两个部分。普通访客浏览前台博客，管理员登录后台后可管理文章、分类、用户等内容。\n\n---\n\n## 一、前台博客（访客视角）\n\n前台是博客对外展示的页面，所有访客均可访问。\n\n### 首页\n\n访问 `/front/index` 进入博客首页，展示最新文章列表、分类导航等信息。\n\n### 文章浏览\n\n- **文章列表**：`/articlelist` 浏览所有已发布文章，可按分类、标签筛选\n- **文章详情**：点击任意文章进入详情页 `/article`，阅读完整内容\n- **时间轴**：`/front/timeline` 按时间线查看所有文章（需登录）\n\n### 分类浏览\n\n`/front/category` 按分类浏览文章，`/front/category/all` 查看全部分类列表。\n\n### 关于页\n\n`/front/about` 展示博主介绍或站点说明。\n\n### 个人中心\n\n`/front/profile` 查看个人信息（需登录）。\n\n---\n\n## 二、后台管理（管理员视角）\n\n访问 `/login` 使用管理员账号登录后，自动跳转到后台管理首页 `/admin/home`。\n\n> 管理员角色 ID 为 1 或 2，普通用户无法进入后台。\n\n### 管理首页\n\n登录后看到的第一个页面，展示系统概览信息或快捷入口。\n\n### 文章管理\n\n后台核心功能，支持以下操作：\n\n1. **写文章**：点击「新建文章」，使用 Markdown 编辑器撰写内容，右侧实时预览\n2. **编辑文章**：点击列表中的编辑按钮，修改已有文章\n3. **文章状态**：\n   - **发布**：文章对外可见，展示在前台\n   - **草稿**：仅后台可见，暂不公开\n   - **私密**：需特定权限才可查看\n4. **文章属性**：可设置封面图、所属分类、标签、置顶、浏览量等\n5. **删除文章**：支持批量删除\n\n### 分类管理\n\n- 新建/编辑/删除文章分类\n- 每个分类有名称和描述\n- 分类用于前台文章归类与导航\n\n### 标签管理\n\n- 新建/编辑/删除标签\n- 每篇文章可打多个标签\n- 前台支持按标签筛选文章\n\n### 评论管理\n\n- 查看所有文章下的评论\n- 审核、删除不当评论\n- 支持回复评论\n\n### 用户管理\n\n- 查看系统所有注册用户\n- 修改用户信息（昵称、邮箱等）\n- 为用户分配角色\n\n### 角色管理\n\n系统采用 **RBAC 权限模型**，通过角色控制菜单权限：\n\n- **管理员（角色 ID=1 或 2）**：可访问所有后台功能\n- **普通用户（角色 ID=3+）**：仅能访问前台博客功能\n- 每个角色可配置不同的菜单权限，后端动态下发\n\n### 系统设置\n\n`/admin/system` 可修改博客全局配置（站点名称、描述、Logo 等）。\n\n### 数据统计\n\n`/admin/echart` 以 ECharts 图表展示文章浏览量、访客趋势等统计数据。\n\n---\n\n## 三、用户注册与登录\n\n### 注册\n\n访问 `/register` 填写用户名、密码、邮箱等信息完成注册。新注册用户默认为普通用户权限。\n\n### 登录\n\n访问 `/login` 输入账号密码登录。系统使用 JWT 认证，登录后 Token 由前端 axios 拦截器自动携带，无需手动处理。\n\n### 权限说明\n\n| 角色 | 可访问页面 |\n|------|-----------|\n| 管理员 | 前台博客 + 后台管理全部功能 |\n| 普通用户 | 前台博客 + 个人中心 + 时间轴 |\n\n未登录用户只能浏览前台公开内容（首页、文章列表、文章详情、分类）。\n\n---\n\n## 四、写文章完整流程\n\n1. 以管理员身份登录后台\n2. 进入文章管理页，点击「新建」\n3. 用 Markdown 语法撰写正文，右侧查看实时渲染效果\n4. 选择分类、添加标签、上传封面图\n5. 设置文章状态为「发布」（或先保存为「草稿」）\n6. 点击保存，文章即发布到前台\n\n---\n\n## 五、动态菜单说明\n\n后台左侧菜单由后端根据当前登录用户的角色动态生成。管理员在「角色管理」中修改某角色的菜单权限后，该角色下的用户重新登录即可看到更新后的菜单。\n','<h1><a id=\"JayBlog__0\"></a>JayBlog 使用指南</h1>\n<p>JayBlog 是一套前后端分离的个人博客系统，分为<strong>前台展示</strong>和<strong>后台管理</strong>两个部分。普通访客浏览前台博客，管理员登录后台后可管理文章、分类、用户等内容。</p>\n<hr />\n<h2><a id=\"_6\"></a>一、前台博客（访客视角）</h2>\n<p>前台是博客对外展示的页面，所有访客均可访问。</p>\n<h3><a id=\"_10\"></a>首页</h3>\n<p>访问 <code>/front/index</code> 进入博客首页，展示最新文章列表、分类导航等信息。</p>\n<h3><a id=\"_14\"></a>文章浏览</h3>\n<ul>\n<li><strong>文章列表</strong>：<code>/articlelist</code> 浏览所有已发布文章，可按分类、标签筛选</li>\n<li><strong>文章详情</strong>：点击任意文章进入详情页 <code>/article</code>，阅读完整内容</li>\n<li><strong>时间轴</strong>：<code>/front/timeline</code> 按时间线查看所有文章（需登录）</li>\n</ul>\n<h3><a id=\"_20\"></a>分类浏览</h3>\n<p><code>/front/category</code> 按分类浏览文章，<code>/front/category/all</code> 查看全部分类列表。</p>\n<h3><a id=\"_24\"></a>关于页</h3>\n<p><code>/front/about</code> 展示博主介绍或站点说明。</p>\n<h3><a id=\"_28\"></a>个人中心</h3>\n<p><code>/front/profile</code> 查看个人信息（需登录）。</p>\n<hr />\n<h2><a id=\"_34\"></a>二、后台管理（管理员视角）</h2>\n<p>访问 <code>/login</code> 使用管理员账号登录后，自动跳转到后台管理首页 <code>/admin/home</code>。</p>\n<blockquote>\n<p>管理员角色 ID 为 1 或 2，普通用户无法进入后台。</p>\n</blockquote>\n<h3><a id=\"_40\"></a>管理首页</h3>\n<p>登录后看到的第一个页面，展示系统概览信息或快捷入口。</p>\n<h3><a id=\"_44\"></a>文章管理</h3>\n<p>后台核心功能，支持以下操作：</p>\n<ol>\n<li><strong>写文章</strong>：点击「新建文章」，使用 Markdown 编辑器撰写内容，右侧实时预览</li>\n<li><strong>编辑文章</strong>：点击列表中的编辑按钮，修改已有文章</li>\n<li><strong>文章状态</strong>：\n<ul>\n<li><strong>发布</strong>：文章对外可见，展示在前台</li>\n<li><strong>草稿</strong>：仅后台可见，暂不公开</li>\n<li><strong>私密</strong>：需特定权限才可查看</li>\n</ul>\n</li>\n<li><strong>文章属性</strong>：可设置封面图、所属分类、标签、置顶、浏览量等</li>\n<li><strong>删除文章</strong>：支持批量删除</li>\n</ol>\n<h3><a id=\"_57\"></a>分类管理</h3>\n<ul>\n<li>新建/编辑/删除文章分类</li>\n<li>每个分类有名称和描述</li>\n<li>分类用于前台文章归类与导航</li>\n</ul>\n<h3><a id=\"_63\"></a>标签管理</h3>\n<ul>\n<li>新建/编辑/删除标签</li>\n<li>每篇文章可打多个标签</li>\n<li>前台支持按标签筛选文章</li>\n</ul>\n<h3><a id=\"_69\"></a>评论管理</h3>\n<ul>\n<li>查看所有文章下的评论</li>\n<li>审核、删除不当评论</li>\n<li>支持回复评论</li>\n</ul>\n<h3><a id=\"_75\"></a>用户管理</h3>\n<ul>\n<li>查看系统所有注册用户</li>\n<li>修改用户信息（昵称、邮箱等）</li>\n<li>为用户分配角色</li>\n</ul>\n<h3><a id=\"_81\"></a>角色管理</h3>\n<p>系统采用 <strong>RBAC 权限模型</strong>，通过角色控制菜单权限：</p>\n<ul>\n<li><strong>管理员（角色 ID=1 或 2）</strong>：可访问所有后台功能</li>\n<li><strong>普通用户（角色 ID=3+）</strong>：仅能访问前台博客功能</li>\n<li>每个角色可配置不同的菜单权限，后端动态下发</li>\n</ul>\n<h3><a id=\"_89\"></a>系统设置</h3>\n<p><code>/admin/system</code> 可修改博客全局配置（站点名称、描述、Logo 等）。</p>\n<h3><a id=\"_93\"></a>数据统计</h3>\n<p><code>/admin/echart</code> 以 ECharts 图表展示文章浏览量、访客趋势等统计数据。</p>\n<hr />\n<h2><a id=\"_99\"></a>三、用户注册与登录</h2>\n<h3><a id=\"_101\"></a>注册</h3>\n<p>访问 <code>/register</code> 填写用户名、密码、邮箱等信息完成注册。新注册用户默认为普通用户权限。</p>\n<h3><a id=\"_105\"></a>登录</h3>\n<p>访问 <code>/login</code> 输入账号密码登录。系统使用 JWT 认证，登录后 Token 由前端 axios 拦截器自动携带，无需手动处理。</p>\n<h3><a id=\"_109\"></a>权限说明</h3>\n<table>\n<thead>\n<tr>\n<th>角色</th>\n<th>可访问页面</th>\n</tr>\n</thead>\n<tbody>\n<tr>\n<td>管理员</td>\n<td>前台博客 + 后台管理全部功能</td>\n</tr>\n<tr>\n<td>普通用户</td>\n<td>前台博客 + 个人中心 + 时间轴</td>\n</tr>\n</tbody>\n</table>\n<p>未登录用户只能浏览前台公开内容（首页、文章列表、文章详情、分类）。</p>\n<hr />\n<h2><a id=\"_120\"></a>四、写文章完整流程</h2>\n<ol>\n<li>以管理员身份登录后台</li>\n<li>进入文章管理页，点击「新建」</li>\n<li>用 Markdown 语法撰写正文，右侧查看实时渲染效果</li>\n<li>选择分类、添加标签、上传封面图</li>\n<li>设置文章状态为「发布」（或先保存为「草稿」）</li>\n<li>点击保存，文章即发布到前台</li>\n</ol>\n<hr />\n<h2><a id=\"_131\"></a>五、动态菜单说明</h2>\n<p>后台左侧菜单由后端根据当前登录用户的角色动态生成。管理员在「角色管理」中修改某角色的菜单权限后，该角色下的用户重新登录即可看到更新后的菜单。</p>\n',51,1,1,2,'2026-06-20 05:28:30','2026-06-21 04:30:42',0),(3,9,5,'','111','1111','','111![TitleJamLogo.jpg](/upload/cover/20260620/581c309224134cffbb650b9da6a4c023.jpg)','<p>111<img src=\"/upload/cover/20260620/581c309224134cffbb650b9da6a4c023.jpg\" alt=\"TitleJamLogo.jpg\" /></p>\n',3,1,0,2,'2026-06-20 22:34:22','2026-06-21 04:26:21',1);
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_like`
--

DROP TABLE IF EXISTS `article_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `article_id` bigint NOT NULL COMMENT '文章id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_like`
--

LOCK TABLES `article_like` WRITE;
/*!40000 ALTER TABLE `article_like` DISABLE KEYS */;
INSERT INTO `article_like` VALUES (3,1,9,'2026-06-20 18:53:54'),(8,3,9,'2026-06-21 03:48:43'),(9,2,9,'2026-06-21 03:48:52');
/*!40000 ALTER TABLE `article_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_tag`
--

DROP TABLE IF EXISTS `article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_at` (`article_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章标签关联';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_tag`
--

LOCK TABLES `article_tag` WRITE;
/*!40000 ALTER TABLE `article_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blog_config`
--

DROP TABLE IF EXISTS `blog_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blog_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `blog_name` varchar(100) NOT NULL COMMENT '博客名称',
  `blog_avatar` varchar(255) DEFAULT '' COMMENT '站点头像',
  `blog_signature` varchar(300) DEFAULT '' COMMENT '个性签名',
  `about_me` longtext COMMENT '关于我页面内容',
  `github` varchar(255) DEFAULT '' COMMENT 'Github地址',
  `qq` varchar(20) DEFAULT '' COMMENT 'QQ',
  `email` varchar(100) DEFAULT '' COMMENT '联系邮箱',
  `allow_comment` tinyint DEFAULT '1' COMMENT '是否开启评论',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='博客全局配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blog_config`
--

LOCK TABLES `blog_config` WRITE;
/*!40000 ALTER TABLE `blog_config` DISABLE KEYS */;
INSERT INTO `blog_config` VALUES (1,'个人技术博客','','热爱编程，记录学习与生活','JayBlog 是一款轻量级前后端分离博客平台，前端使用 Vue 2 + Element UI 构建管理后台与前台展示页面，后端基于 Spring Boot 2.7 + MyBatis-Plus 实现 RESTful API。系统采用 JWT 无状态认证，支持 RBAC 角色权限模型与动态路由注册，内置 Markdown 富文本编辑、文章草稿发布、分类标签体系、评论管理、访客浏览统计及 ECharts 数据看板等功能。项目基于自研脚手架快速搭建，适合作为个人技术博客或小型内容管理系统的起步工程。','https://gitee.com/akaywj/jay.-blog','1224973233','',1,'2026-06-20 01:00:36','2026-06-20 01:00:36');
/*!40000 ALTER TABLE `blog_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `sort` int DEFAULT '0' COMMENT '排序权重',
  `status` tinyint DEFAULT '1' COMMENT '0隐藏 1显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (1,'科技',1,1,'2026-06-20 01:33:04','2026-06-20 22:12:41',0),(5,'日常',0,1,'2026-06-20 22:34:00','2026-06-20 22:34:00',0);
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint NOT NULL COMMENT '关联文章ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID，一级评论为NULL',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-正常',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(32) NOT NULL COMMENT '角色名称',
  `role_code` varchar(32) NOT NULL COMMENT '角色标识：admin/manager/user',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'超级管理员','admin','最高权限','2026-06-09 10:52:32','2026-06-09 10:52:32'),(2,'管理员(员工)','manager','普通管理权限','2026-06-09 10:52:32','2026-06-09 16:46:50'),(3,'普通用户','user','普通用户权限','2026-06-09 10:52:32','2026-06-09 10:52:32');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_permission`
--

DROP TABLE IF EXISTS `sys_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_permission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `parent_id` int DEFAULT '0' COMMENT '父级权限ID',
  `name` varchar(64) NOT NULL COMMENT '权限名称',
  `code` varchar(128) NOT NULL COMMENT '权限标识（唯一）',
  `type` char(1) NOT NULL COMMENT '权限类型 M-菜单 B-按钮',
  `path` varchar(255) DEFAULT NULL COMMENT '路由路径（菜单用）',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径（菜单用）',
  `icon` varchar(64) DEFAULT NULL COMMENT '图标（菜单用）',
  `sort` int DEFAULT '0' COMMENT '排序',
  `is_valid` char(1) DEFAULT 'Y' COMMENT '是否有效 Y-有效 N-无效',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (2,0,'用户管理','user','M','admin/users','Users','el-icon-user-solid',2,'Y','2026-06-09 15:14:56','2026-06-20 03:45:45'),(4,2,'查看用户','user:list','B',NULL,NULL,NULL,1,'Y','2026-06-09 15:14:56','2026-06-09 15:14:56'),(5,2,'新增用户','user:add','B',NULL,NULL,NULL,2,'Y','2026-06-09 15:14:56','2026-06-09 15:14:56'),(6,2,'编辑用户','user:edit','B',NULL,NULL,NULL,3,'Y','2026-06-09 15:14:56','2026-06-09 15:14:56'),(7,2,'删除用户','user:delete','B',NULL,NULL,NULL,4,'Y','2026-06-09 15:14:56','2026-06-09 15:14:56'),(9,0,'角色管理','role','M','/admin/roles','Roles','el-icon-s-custom',3,'Y','2026-06-09 15:50:05','2026-06-20 02:24:31'),(10,9,'查看角色','role:list','B',NULL,NULL,NULL,1,'Y','2026-06-09 15:50:05','2026-06-09 15:50:05'),(11,9,'新增角色','role:add','B',NULL,NULL,NULL,2,'Y','2026-06-09 15:50:05','2026-06-09 15:50:05'),(12,9,'编辑角色','role:edit','B',NULL,NULL,NULL,3,'Y','2026-06-09 15:50:05','2026-06-09 15:50:05'),(13,9,'删除角色','role:delete','B',NULL,NULL,NULL,4,'Y','2026-06-09 15:50:05','2026-06-09 15:50:05'),(99,0,'系统设置','system','M','/admin/system','System','el-icon-setting',4,'Y','2026-06-09 15:14:56','2026-06-20 02:24:38'),(100,0,'文章管理','article','M','/admin/article','Article','el-icon-document',1,'Y','2026-06-20 01:18:18','2026-06-20 02:24:41'),(101,100,'文章列表','article:list','B',NULL,NULL,NULL,1,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(102,100,'新增文章','article:add','B',NULL,NULL,NULL,2,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(103,100,'编辑文章','article:edit','B',NULL,NULL,NULL,3,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(104,100,'删除文章','article:delete','B',NULL,NULL,NULL,4,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(105,100,'批量删除','article:batchDelete','B',NULL,NULL,NULL,5,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(106,100,'修改置顶','article:top','B',NULL,NULL,NULL,6,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(107,100,'修改状态','article:status','B',NULL,NULL,NULL,7,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(108,0,'分类管理','category','M','/admin/category','Category','el-icon-folder-opened',5,'Y','2026-06-20 01:18:18','2026-06-20 02:24:43'),(109,108,'分类列表','category:list','B',NULL,NULL,NULL,1,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(110,108,'新增分类','category:add','B',NULL,NULL,NULL,2,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(111,108,'编辑分类','category:edit','B',NULL,NULL,NULL,3,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(112,108,'删除分类','category:delete','B',NULL,NULL,NULL,4,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(113,0,'标签管理','tag','M','/admin/tag','Tag','el-icon-price-tag',6,'Y','2026-06-20 01:18:18','2026-06-20 02:24:45'),(114,113,'标签列表','tag:list','B',NULL,NULL,NULL,1,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(115,113,'新增标签','tag:add','B',NULL,NULL,NULL,2,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(116,113,'编辑标签','tag:edit','B',NULL,NULL,NULL,3,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(117,113,'删除标签','tag:delete','B',NULL,NULL,NULL,4,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(118,0,'评论管理','comment','M','/admin/comment','Comment','el-icon-chat-line-round',7,'Y','2026-06-20 01:18:18','2026-06-20 02:24:49'),(119,118,'评论列表','comment:list','B',NULL,NULL,NULL,1,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(120,118,'审核评论','comment:audit','B',NULL,NULL,NULL,2,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18'),(121,118,'删除评论','comment:delete','B',NULL,NULL,NULL,3,'Y','2026-06-20 01:18:18','2026-06-20 01:18:18');
/*!40000 ALTER TABLE `sys_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_permission`
--

DROP TABLE IF EXISTS `sys_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_permission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` int NOT NULL COMMENT '角色ID（和你的role表id关联）',
  `permission_id` int NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (22,2,2,'2026-06-20 00:38:56'),(23,2,4,'2026-06-20 00:38:56'),(24,2,9,'2026-06-20 00:38:56'),(25,2,10,'2026-06-20 00:38:56'),(26,2,99,'2026-06-20 00:38:56'),(61,1,100,'2026-06-21 04:26:34'),(62,1,101,'2026-06-21 04:26:34'),(63,1,102,'2026-06-21 04:26:34'),(64,1,103,'2026-06-21 04:26:34'),(65,1,104,'2026-06-21 04:26:34'),(66,1,105,'2026-06-21 04:26:34'),(67,1,106,'2026-06-21 04:26:34'),(68,1,107,'2026-06-21 04:26:34'),(69,1,2,'2026-06-21 04:26:34'),(70,1,4,'2026-06-21 04:26:34'),(71,1,5,'2026-06-21 04:26:34'),(72,1,6,'2026-06-21 04:26:34'),(73,1,7,'2026-06-21 04:26:34'),(74,1,9,'2026-06-21 04:26:34'),(75,1,10,'2026-06-21 04:26:34'),(76,1,11,'2026-06-21 04:26:34'),(77,1,12,'2026-06-21 04:26:34'),(78,1,13,'2026-06-21 04:26:34'),(79,1,99,'2026-06-21 04:26:34'),(80,1,108,'2026-06-21 04:26:34'),(81,1,109,'2026-06-21 04:26:34'),(82,1,110,'2026-06-21 04:26:34'),(83,1,111,'2026-06-21 04:26:34'),(84,1,112,'2026-06-21 04:26:34'),(85,1,113,'2026-06-21 04:26:34'),(86,1,114,'2026-06-21 04:26:34'),(87,1,115,'2026-06-21 04:26:34'),(88,1,116,'2026-06-21 04:26:34'),(89,1,117,'2026-06-21 04:26:34'),(90,1,118,'2026-06-21 04:26:34'),(91,1,119,'2026-06-21 04:26:34'),(92,1,120,'2026-06-21 04:26:34'),(93,1,121,'2026-06-21 04:26:34'),(100,3,101,'2026-06-21 04:29:15'),(101,3,6,'2026-06-21 04:29:15'),(102,3,99,'2026-06-21 04:29:15'),(103,3,109,'2026-06-21 04:29:15'),(104,3,114,'2026-06-21 04:29:15'),(105,3,119,'2026-06-21 04:29:15'),(106,3,121,'2026-06-21 04:29:15');
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL COMMENT '标签名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章标签';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (1,'日记','2026-06-20 23:06:13','2026-06-20 23:06:13',0),(2,'闲聊','2026-06-20 23:06:20','2026-06-20 23:06:20',0),(3,'分享','2026-06-20 23:06:24','2026-06-20 23:06:24',0);
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `no` varchar(32) DEFAULT NULL COMMENT '账号',
  `name` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `age` int DEFAULT NULL COMMENT '年龄',
  `sex` int DEFAULT NULL COMMENT '性别 1男0女',
  `phone` varchar(16) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像图片地址',
  `email` varchar(100) DEFAULT NULL COMMENT '电子邮箱',
  `role_id` int DEFAULT NULL COMMENT '角色ID',
  `is_valid` char(1) DEFAULT 'Y' COMMENT '是否有效 Y/N',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_no` (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (9,'U990321662136','admin','lueSGJZetyySpUndWjMBEg==',111,NULL,'19999999999',NULL,NULL,1,'Y','2026-06-09 15:32:01','2026-06-21 03:23:13');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_article_full`
--

DROP TABLE IF EXISTS `v_article_full`;
/*!50001 DROP VIEW IF EXISTS `v_article_full`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_article_full` AS SELECT 
 1 AS `id`,
 1 AS `user_id`,
 1 AS `category_id`,
 1 AS `tag_ids`,
 1 AS `title`,
 1 AS `summary`,
 1 AS `cover`,
 1 AS `content_md`,
 1 AS `content_html`,
 1 AS `view_count`,
 1 AS `like_count`,
 1 AS `is_top`,
 1 AS `status`,
 1 AS `create_time`,
 1 AS `update_time`,
 1 AS `is_deleted`,
 1 AS `category_name`,
 1 AS `tag_ids_str`,
 1 AS `tag_names_str`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `visitor`
--

DROP TABLE IF EXISTS `visitor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visitor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ip` varchar(50) NOT NULL COMMENT '访客IP',
  `address` varchar(100) DEFAULT '' COMMENT 'IP归属地',
  `visit_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ip` (`ip`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='访客访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visitor`
--

LOCK TABLES `visitor` WRITE;
/*!40000 ALTER TABLE `visitor` DISABLE KEYS */;
INSERT INTO `visitor` VALUES (1,'127.0.0.1','','2026-06-20 22:25:16'),(2,'127.0.0.1','','2026-06-21 00:00:10');
/*!40000 ALTER TABLE `visitor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `v_article_full`
--

/*!50001 DROP VIEW IF EXISTS `v_article_full`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_article_full` AS select `a`.`id` AS `id`,`a`.`user_id` AS `user_id`,`a`.`category_id` AS `category_id`,`a`.`tag_ids` AS `tag_ids`,`a`.`title` AS `title`,`a`.`summary` AS `summary`,`a`.`cover` AS `cover`,`a`.`content_md` AS `content_md`,`a`.`content_html` AS `content_html`,`a`.`view_count` AS `view_count`,`a`.`like_count` AS `like_count`,`a`.`is_top` AS `is_top`,`a`.`status` AS `status`,`a`.`create_time` AS `create_time`,`a`.`update_time` AS `update_time`,`a`.`is_deleted` AS `is_deleted`,`c`.`name` AS `category_name`,group_concat(`t`.`id` separator ',') AS `tag_ids_str`,group_concat(`t`.`name` separator ',') AS `tag_names_str` from ((`article` `a` left join `category` `c` on(((`a`.`category_id` = `c`.`id`) and (`c`.`is_deleted` = 0)))) left join `tag` `t` on(((0 <> find_in_set(`t`.`id`,`a`.`tag_ids`)) and (`t`.`is_deleted` = 0)))) where (`a`.`is_deleted` = 0) group by `a`.`id` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-21 13:09:35
