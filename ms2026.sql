/*
 Navicat Premium Dump SQL

 Source Server         : MyMacLocalhost3306
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : ms2026

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 09/06/2026 16:47:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(32) NOT NULL COMMENT '角色名称',
  `role_code` varchar(32) NOT NULL COMMENT '角色标识：admin/manager/user',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';

-- ----------------------------
-- Records of role
-- ----------------------------
BEGIN;
INSERT INTO `role` (`id`, `role_name`, `role_code`, `description`, `create_time`, `update_time`) VALUES (1, '超级管理员', 'admin', '最高权限', '2026-06-09 10:52:32', '2026-06-09 10:52:32');
INSERT INTO `role` (`id`, `role_name`, `role_code`, `description`, `create_time`, `update_time`) VALUES (2, '管理员(员工)', 'manager', '普通管理权限', '2026-06-09 10:52:32', '2026-06-09 16:46:50');
INSERT INTO `role` (`id`, `role_name`, `role_code`, `description`, `create_time`, `update_time`) VALUES (3, '普通用户', 'user', '普通用户权限', '2026-06-09 10:52:32', '2026-06-09 10:52:32');
COMMIT;

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
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
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限表';

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
BEGIN;
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (2, 0, '用户管理', 'user', 'M', '/users', 'Users', 'el-icon-user-solid', 2, 'Y', '2026-06-09 15:14:56', '2026-06-09 15:50:05');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (4, 2, '查看用户', 'user:list', 'B', NULL, NULL, NULL, 1, 'Y', '2026-06-09 15:14:56', '2026-06-09 15:14:56');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (5, 2, '新增用户', 'user:add', 'B', NULL, NULL, NULL, 2, 'Y', '2026-06-09 15:14:56', '2026-06-09 15:14:56');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (6, 2, '编辑用户', 'user:edit', 'B', NULL, NULL, NULL, 3, 'Y', '2026-06-09 15:14:56', '2026-06-09 15:14:56');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (7, 2, '删除用户', 'user:delete', 'B', NULL, NULL, NULL, 4, 'Y', '2026-06-09 15:14:56', '2026-06-09 15:14:56');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (9, 0, '角色管理', 'role', 'M', '/roles', 'Roles', 'el-icon-s-custom', 3, 'Y', '2026-06-09 15:50:05', '2026-06-09 15:50:05');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (10, 9, '查看角色', 'role:list', 'B', NULL, NULL, NULL, 1, 'Y', '2026-06-09 15:50:05', '2026-06-09 15:50:05');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (11, 9, '新增角色', 'role:add', 'B', NULL, NULL, NULL, 2, 'Y', '2026-06-09 15:50:05', '2026-06-09 15:50:05');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (12, 9, '编辑角色', 'role:edit', 'B', NULL, NULL, NULL, 3, 'Y', '2026-06-09 15:50:05', '2026-06-09 15:50:05');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (13, 9, '删除角色', 'role:delete', 'B', NULL, NULL, NULL, 4, 'Y', '2026-06-09 15:50:05', '2026-06-09 15:50:05');
INSERT INTO `sys_permission` (`id`, `parent_id`, `name`, `code`, `type`, `path`, `component`, `icon`, `sort`, `is_valid`, `create_time`, `update_time`) VALUES (99, 0, '系统设置', 'system', 'M', '/system', 'System', 'el-icon-setting', 4, 'Y', '2026-06-09 15:14:56', '2026-06-09 16:09:47');
COMMIT;

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` int NOT NULL COMMENT '角色ID（和你的role表id关联）',
  `permission_id` int NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色权限关联表';

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (1, 1, 99, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (2, 1, 2, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (3, 1, 3, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (4, 1, 4, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (5, 1, 5, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (6, 1, 6, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (7, 1, 7, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (8, 2, 1, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (9, 2, 2, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (10, 2, 4, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (11, 3, 1, '2026-06-09 15:15:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (12, 1, 8, '2026-06-09 15:50:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (13, 1, 9, '2026-06-09 15:50:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (14, 1, 10, '2026-06-09 15:50:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (15, 1, 11, '2026-06-09 15:50:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (16, 1, 12, '2026-06-09 15:50:05');
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_time`) VALUES (17, 1, 13, '2026-06-09 15:50:05');
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `no` varchar(32) DEFAULT NULL COMMENT '账号',
  `name` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `age` int DEFAULT NULL COMMENT '年龄',
  `sex` int DEFAULT NULL COMMENT '性别 1男0女',
  `phone` varchar(16) DEFAULT NULL COMMENT '手机号',
  `role_id` int DEFAULT NULL COMMENT '角色ID',
  `is_valid` char(1) DEFAULT 'Y' COMMENT '是否有效 Y/N',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_no` (`no`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`, `is_valid`, `create_time`, `update_time`) VALUES (8, 'U988762086203', '123', 'ICy5YqxZB1uWSwcVLSNLcA==', 1, 1, '16554445555', 3, 'Y', '2026-06-09 15:06:02', '2026-06-09 15:06:02');
INSERT INTO `user` (`id`, `no`, `name`, `password`, `age`, `sex`, `phone`, `role_id`, `is_valid`, `create_time`, `update_time`) VALUES (9, 'U990321662136', '111', 'lueSGJZetyySpUndWjMBEg==', 111, NULL, '19999999999', 1, 'Y', '2026-06-09 15:32:01', '2026-06-09 15:32:25');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
