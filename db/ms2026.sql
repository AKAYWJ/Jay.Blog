-- MySQL dump 10.13  Distrib 8.0.39, for macos14 (arm64)
--
-- Host: localhost    Database: ms2026
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
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '客户ID(主键)',
  `user_id` int NOT NULL COMMENT '关联用户ID(一对一)',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '身份证号',
  `member_level` enum('普通会员','银卡会员','金卡会员','钻石会员') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '普通会员' COMMENT '会员等级',
  `integral` int DEFAULT '0' COMMENT '积分',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '账户余额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`user_id`) USING BTREE,
  UNIQUE KEY `uk_id_card` (`id_card`) USING BTREE,
  CONSTRAINT `fk_customer_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_id_card` CHECK ((length(`id_card`) = 18))
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='客户业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,8,'张三1','333333222211112222','普通会员',0,0.00,'2026-06-14 13:22:38','2026-06-14 13:22:38');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employee`
--

DROP TABLE IF EXISTS `employee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employee` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '员工编号',
  `user_id` int NOT NULL COMMENT '关联用户ID(一对一)',
  `emp_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工姓名',
  `id_card` char(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证号',
  `position` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '岗位',
  `entry_date` date NOT NULL COMMENT '入职日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_id_card` (`id_card`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `fk_employee_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_emp_id_card` CHECK ((length(`id_card`) = 18))
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='员工业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employee`
--

LOCK TABLES `employee` WRITE;
/*!40000 ALTER TABLE `employee` DISABLE KEYS */;
INSERT INTO `employee` VALUES (21,9,'ywj','333333444455556666','Master','2026-06-14','2026-06-14 14:37:43','2026-06-14 14:37:43');
/*!40000 ALTER TABLE `employee` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hotel`
--

DROP TABLE IF EXISTS `hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hotel` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '酒店编号',
  `hotel_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '酒店名称',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '酒店地址',
  `contact_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '酒店描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `hotel_name` (`hotel_name`) USING BTREE,
  UNIQUE KEY `address` (`address`) USING BTREE,
  UNIQUE KEY `contact_number` (`contact_number`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='酒店信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hotel`
--

LOCK TABLES `hotel` WRITE;
/*!40000 ALTER TABLE `hotel` DISABLE KEYS */;
INSERT INTO `hotel` VALUES (1,'北京朝阳国际酒店','北京市朝阳区建国路88号','010-88886666','四星级商务酒店，紧邻CBD','2026-06-10 17:54:00','2026-06-10 17:54:00');
/*!40000 ALTER TABLE `hotel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `customer_id` int NOT NULL COMMENT '客户编号',
  `employee_id` int NOT NULL COMMENT '办理员工编号',
  `room_id` int NOT NULL COMMENT '房间编号',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `check_out_date` date NOT NULL COMMENT '退房日期',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `order_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '订单状态：已完成/待入住/已预订/已取消',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `customer_id` (`customer_id`) USING BTREE,
  KEY `room_id` (`room_id`) USING BTREE,
  KEY `employee_id` (`employee_id`) USING BTREE,
  KEY `idx_check_date` (`check_in_date`,`check_out_date`) USING BTREE,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,21,2,'2026-06-07','2026-06-11',900.00,'已支付','2026-06-09 10:00:00','2026-06-09 10:00:00'),(2,1,21,1,'2026-06-12','2026-06-16',1260.00,'已支付','2026-06-14 10:00:00','2026-06-14 10:00:00'),(3,1,21,1,'2026-06-17','2026-06-21',760.00,'待支付','2026-06-19 10:00:00','2026-06-19 10:00:00'),(4,1,21,1,'2026-06-22','2026-06-26',1360.00,'已支付','2026-06-24 10:00:00','2026-06-24 10:00:00'),(5,1,21,1,'2026-06-28','2026-07-02',1440.00,'已支付','2026-06-30 10:00:00','2026-06-30 10:00:00'),(6,1,21,1,'2026-07-02','2026-07-06',1560.00,'已支付','2026-07-04 10:00:00','2026-07-04 10:00:00'),(7,1,21,1,'2026-07-07','2026-07-11',1760.00,'待支付','2026-07-09 10:00:00','2026-07-09 10:00:00'),(8,1,21,1,'2026-07-12','2026-07-16',920.00,'已支付','2026-07-14 10:00:00','2026-07-14 10:00:00'),(9,1,21,1,'2026-07-17','2026-07-21',1180.00,'已支付','2026-07-19 10:00:00','2026-07-19 10:00:00'),(10,1,21,1,'2026-07-22','2026-07-26',2400.00,'待支付','2026-07-24 10:00:00','2026-07-24 10:00:00'),(11,1,21,1,'2026-07-29','2026-08-02',720.00,'已支付','2026-07-31 10:00:00','2026-07-31 10:00:00'),(12,1,21,1,'2026-08-02','2026-08-06',1240.00,'已支付','2026-08-04 10:00:00','2026-08-04 10:00:00'),(13,1,21,1,'2026-08-07','2026-08-11',1500.00,'待支付','2026-08-09 10:00:00','2026-08-09 10:00:00'),(14,1,21,1,'2026-08-12','2026-08-16',1960.00,'已支付','2026-08-14 10:00:00','2026-08-14 10:00:00'),(15,1,21,1,'2026-08-17','2026-08-21',880.00,'已支付','2026-08-19 10:00:00','2026-08-19 10:00:00'),(16,1,21,1,'2026-08-22','2026-08-26',1100.00,'待支付','2026-08-24 10:00:00','2026-08-24 10:00:00'),(17,1,21,1,'2026-08-29','2026-09-02',1380.00,'已支付','2026-08-31 10:00:00','2026-08-31 10:00:00'),(18,1,21,1,'2026-09-02','2026-09-06',598.00,'已支付','2026-09-04 10:00:00','2026-09-04 10:00:00'),(19,1,21,1,'2026-09-07','2026-09-11',1580.00,'待支付','2026-09-09 10:00:00','2026-09-09 10:00:00'),(20,1,21,1,'2026-09-12','2026-09-16',2760.00,'已支付','2026-09-14 10:00:00','2026-09-14 10:00:00'),(27,10,1,2,'2026-06-22','2026-06-23',580.00,'待入住','2026-06-22 18:55:02','2026-06-22 18:55:02'),(28,10,1,2,'2026-06-22','2026-06-23',580.00,'待入住','2026-06-22 18:57:45','2026-06-22 18:57:45'),(29,10,1,2,'2026-06-22','2026-06-23',580.00,'待入住','2026-06-22 19:00:14','2026-06-22 19:00:14'),(30,10,1,2,'2026-06-22','2026-06-23',580.00,'已支付','2026-06-22 19:00:43','2026-06-22 19:18:26'),(31,10,1,1,'2026-06-22','2026-06-24',900.00,'已支付','2026-06-22 19:51:08','2026-06-22 19:51:29');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '支付编号',
  `order_id` int NOT NULL COMMENT '关联订单编号',
  `payment_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '支付方式',
  `payment_date` datetime NOT NULL COMMENT '支付时间',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '支付状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE,
  KEY `idx_payment_date` (`payment_date`) USING BTREE,
  CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='支付记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,1,900.00,'支付宝','2026-06-09 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(2,2,1260.00,'微信支付','2026-06-14 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(3,3,760.00,'银行卡','2026-06-19 10:00:00','待支付','2026-06-10 17:54:00','2026-06-10 17:54:00'),(4,4,1360.00,'支付宝','2026-06-24 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(5,5,1440.00,'微信支付','2026-06-30 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(6,6,1560.00,'银行卡','2026-07-04 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(7,7,1760.00,'支付宝','2026-07-09 10:00:00','待支付','2026-06-10 17:54:00','2026-06-10 17:54:00'),(8,8,920.00,'微信支付','2026-07-14 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(9,9,1180.00,'银行卡','2026-07-19 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(10,10,2400.00,'支付宝','2026-07-24 10:00:00','待支付','2026-06-10 17:54:00','2026-06-10 17:54:00'),(11,11,720.00,'微信支付','2026-07-31 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(12,12,1240.00,'银行卡','2026-08-04 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(13,13,1500.00,'支付宝','2026-08-09 10:00:00','待支付','2026-06-10 17:54:00','2026-06-10 17:54:00'),(14,14,1960.00,'微信支付','2026-08-14 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(15,15,880.00,'银行卡','2026-08-19 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(16,16,1100.00,'支付宝','2026-08-24 10:00:00','待支付','2026-06-10 17:54:00','2026-06-10 17:54:00'),(17,17,1380.00,'微信支付','2026-08-31 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(18,18,598.00,'银行卡','2026-09-04 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00'),(19,19,1580.00,'支付宝','2026-09-09 10:00:00','待支付','2026-06-10 17:54:00','2026-06-10 17:54:00'),(20,20,2760.00,'微信支付','2026-09-14 10:00:00','支付成功','2026-06-10 17:54:00','2026-06-10 17:54:00');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色标识：admin/manager/user',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'超级管理员','admin','最高权限','2026-06-09 10:52:32','2026-06-09 10:52:32'),(2,'管理员','manager','普通管理权限','2026-06-09 10:52:32','2026-06-14 13:42:01'),(3,'员工','employee','普通员工权限','2026-06-14 13:42:24','2026-06-14 13:42:24'),(4,'普通用户','user','普通用户权限','2026-06-09 10:52:32','2026-06-14 13:42:05');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '房间编号',
  `hotel_id` int NOT NULL COMMENT '所属酒店编号',
  `room_number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间号',
  `room_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间类型',
  `price` decimal(10,2) NOT NULL COMMENT '单日房价',
  `room_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '空闲' COMMENT '房间状态：空闲/已入住/已预订/维修/清洁中',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_room_number` (`hotel_id`,`room_number`) USING BTREE,
  KEY `hotel_id` (`hotel_id`) USING BTREE,
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='客房信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (1,1,'1001','标准双人间',450.00,'空闲','2026-06-10 17:54:00','2026-06-10 17:54:00'),(2,1,'1002','商务大床房',580.00,'空闲','2026-06-10 17:54:00','2026-06-10 17:54:00'),(3,1,'1003','标准单人间',380.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(4,1,'1004','豪华双人间',680.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(5,1,'1005','家庭房',720.00,'空闲','2026-06-10 17:54:00','2026-06-10 17:54:00'),(6,1,'1006','湖景大床房',780.00,'空闲','2026-06-10 17:54:00','2026-06-10 17:54:00'),(7,1,'1007','行政大床房',880.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(8,1,'1008','标准双人间',460.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(9,1,'1009','商务大床房',590.00,'空闲','2026-06-10 17:54:00','2026-06-10 17:54:00'),(10,1,'1010','豪华套房',1200.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(11,1,'1011','标准单人间',360.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(12,1,'1012','景观双人间',620.00,'空闲','2026-06-10 17:54:00','2026-06-10 17:54:00'),(13,1,'1013','亲子房',750.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(14,1,'1014','商务套房',980.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(15,1,'1015','标准双人间',440.00,'空闲','2026-06-10 17:54:00','2026-06-10 17:54:00'),(16,1,'1016','大床房',550.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(17,1,'1017','主题房',690.00,'空闲','2026-06-10 17:54:00','2026-06-10 17:54:00'),(18,1,'1018','经济单人间',299.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(19,1,'1019','豪华大床房',790.00,'已预订','2026-06-10 17:54:00','2026-06-10 17:54:00'),(20,1,'1020','观景套房',1380.00,'空闲','2026-06-10 17:54:00','2026-06-10 17:54:00');
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
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
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限标识（唯一）',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限类型 M-菜单 B-按钮',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '路由路径（菜单用）',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '组件路径（菜单用）',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '图标（菜单用）',
  `sort` int DEFAULT '0' COMMENT '排序',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'Y' COMMENT '是否有效 Y-有效 N-无效',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_permission`
--

LOCK TABLES `sys_permission` WRITE;
/*!40000 ALTER TABLE `sys_permission` DISABLE KEYS */;
INSERT INTO `sys_permission` VALUES (2,0,'用户管理','user','M','/users','Users','el-icon-user-solid',2,'Y','2026-06-09 15:14:56','2026-06-09 15:50:05'),(4,2,'查看用户','user:list','B',NULL,NULL,NULL,1,'Y','2026-06-09 15:14:56','2026-06-09 15:14:56'),(5,2,'新增用户','user:add','B',NULL,NULL,NULL,2,'Y','2026-06-09 15:14:56','2026-06-09 15:14:56'),(6,2,'编辑用户','user:edit','B',NULL,NULL,NULL,3,'Y','2026-06-09 15:14:56','2026-06-09 15:14:56'),(7,2,'删除用户','user:delete','B',NULL,NULL,NULL,4,'Y','2026-06-09 15:14:56','2026-06-09 15:14:56'),(9,0,'角色管理','role','M','/roles','Roles','el-icon-s-custom',3,'Y','2026-06-09 15:50:05','2026-06-09 15:50:05'),(10,9,'查看角色','role:list','B',NULL,NULL,NULL,1,'Y','2026-06-09 15:50:05','2026-06-09 15:50:05'),(11,9,'新增角色','role:add','B',NULL,NULL,NULL,2,'Y','2026-06-09 15:50:05','2026-06-09 15:50:05'),(12,9,'编辑角色','role:edit','B',NULL,NULL,NULL,3,'Y','2026-06-09 15:50:05','2026-06-09 15:50:05'),(13,9,'删除角色','role:delete','B',NULL,NULL,NULL,4,'Y','2026-06-09 15:50:05','2026-06-09 15:50:05'),(99,0,'系统设置','system','M','/system','System','el-icon-setting',4,'Y','2026-06-09 15:14:56','2026-06-09 16:09:47'),(100,0,'客户管理','customer','M','/customer','Customer','el-icon-s-custom',10,'Y','2026-06-12 09:24:13','2026-06-12 09:33:53'),(101,100,'查看客户','customer:list','B',NULL,NULL,NULL,1,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(102,100,'新增客户','customer:add','B',NULL,NULL,NULL,2,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(103,100,'编辑客户','customer:edit','B',NULL,NULL,NULL,3,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(104,100,'删除客户','customer:delete','B',NULL,NULL,NULL,4,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(110,0,'员工管理','employee','M','/employee','Employee','el-icon-headset',11,'Y','2026-06-12 09:24:13','2026-06-12 09:33:33'),(111,110,'查看员工','employee:list','B',NULL,NULL,NULL,1,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(112,110,'新增员工','employee:add','B',NULL,NULL,NULL,2,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(113,110,'编辑员工','employee:edit','B',NULL,NULL,NULL,3,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(114,110,'删除员工','employee:delete','B',NULL,NULL,NULL,4,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(120,0,'酒店管理','hotel','M','/hotel','Hotel','el-icon-s-home',12,'Y','2026-06-12 09:24:13','2026-06-12 09:34:19'),(121,120,'查看酒店','hotel:list','B',NULL,NULL,NULL,1,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(122,120,'新增酒店','hotel:add','B',NULL,NULL,NULL,2,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(123,120,'编辑酒店','hotel:edit','B',NULL,NULL,NULL,3,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(124,120,'删除酒店','hotel:delete','B',NULL,NULL,NULL,4,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(130,0,'客房管理','room','M','/room','Room','el-icon-s-home',13,'Y','2026-06-12 09:24:13','2026-06-12 09:35:29'),(131,130,'查看客房','room:list','B',NULL,NULL,NULL,1,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(132,130,'新增客房','room:add','B',NULL,NULL,NULL,2,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(133,130,'编辑客房','room:edit','B',NULL,NULL,NULL,3,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(134,130,'删除客房','room:delete','B',NULL,NULL,NULL,4,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(140,0,'订单管理','orders','M','/orders','Orders','el-icon-s-goods',14,'Y','2026-06-12 09:24:13','2026-06-12 09:34:22'),(141,140,'查看订单','orders:list','B',NULL,NULL,NULL,1,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(142,140,'新增订单','orders:add','B',NULL,NULL,NULL,2,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(143,140,'编辑订单','orders:edit','B',NULL,NULL,NULL,3,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(144,140,'删除订单','orders:delete','B',NULL,NULL,NULL,4,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(150,0,'支付记录','payment','M','/payment','Payment','el-icon-money',15,'Y','2026-06-12 09:24:13','2026-06-12 09:35:26'),(151,150,'查看支付','payment:list','B',NULL,NULL,NULL,1,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(152,150,'新增支付','payment:add','B',NULL,NULL,NULL,2,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(153,150,'编辑支付','payment:edit','B',NULL,NULL,NULL,3,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13'),(154,150,'删除支付','payment:delete','B',NULL,NULL,NULL,4,'Y','2026-06-12 09:24:13','2026-06-12 09:24:13');
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
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE,
  KEY `idx_permission_id` (`permission_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='角色权限关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_permission`
--

LOCK TABLES `sys_role_permission` WRITE;
/*!40000 ALTER TABLE `sys_role_permission` DISABLE KEYS */;
INSERT INTO `sys_role_permission` VALUES (8,2,1,'2026-06-09 15:15:05'),(9,2,2,'2026-06-09 15:15:05'),(10,2,4,'2026-06-09 15:15:05'),(11,3,1,'2026-06-09 15:15:05'),(48,1,2,'2026-06-14 13:16:08'),(49,1,4,'2026-06-14 13:16:08'),(50,1,5,'2026-06-14 13:16:08'),(51,1,6,'2026-06-14 13:16:08'),(52,1,7,'2026-06-14 13:16:08'),(53,1,9,'2026-06-14 13:16:08'),(54,1,10,'2026-06-14 13:16:08'),(55,1,11,'2026-06-14 13:16:08'),(56,1,12,'2026-06-14 13:16:08'),(57,1,13,'2026-06-14 13:16:08'),(58,1,99,'2026-06-14 13:16:08'),(59,1,100,'2026-06-14 13:16:08'),(60,1,101,'2026-06-14 13:16:08'),(61,1,102,'2026-06-14 13:16:08'),(62,1,103,'2026-06-14 13:16:08'),(63,1,104,'2026-06-14 13:16:08'),(64,1,110,'2026-06-14 13:16:08'),(65,1,111,'2026-06-14 13:16:08'),(66,1,112,'2026-06-14 13:16:08'),(67,1,113,'2026-06-14 13:16:08'),(68,1,114,'2026-06-14 13:16:08'),(69,1,120,'2026-06-14 13:16:08'),(70,1,121,'2026-06-14 13:16:08'),(71,1,122,'2026-06-14 13:16:08'),(72,1,123,'2026-06-14 13:16:08'),(73,1,124,'2026-06-14 13:16:08'),(74,1,130,'2026-06-14 13:16:08'),(75,1,131,'2026-06-14 13:16:08'),(76,1,132,'2026-06-14 13:16:08'),(77,1,133,'2026-06-14 13:16:08'),(78,1,134,'2026-06-14 13:16:08'),(79,1,140,'2026-06-14 13:16:08'),(80,1,141,'2026-06-14 13:16:08'),(81,1,142,'2026-06-14 13:16:08'),(82,1,143,'2026-06-14 13:16:08'),(83,1,144,'2026-06-14 13:16:08'),(84,1,150,'2026-06-14 13:16:08'),(85,1,151,'2026-06-14 13:16:08'),(86,1,152,'2026-06-14 13:16:08'),(87,1,153,'2026-06-14 13:16:08'),(88,1,154,'2026-06-14 13:16:08');
/*!40000 ALTER TABLE `sys_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '账号',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `age` int DEFAULT NULL COMMENT '年龄',
  `sex` int DEFAULT NULL COMMENT '性别 1男0女',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `role_id` int DEFAULT NULL COMMENT '角色ID',
  `is_valid` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'Y' COMMENT '是否有效 Y/N',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_no` (`no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (8,'U988762086203','123','ICy5YqxZB1uWSwcVLSNLcA==',1,1,'16554445555',3,'Y','2026-06-09 15:06:02','2026-06-09 15:06:02'),(9,'U990321662136','111','lueSGJZetyySpUndWjMBEg==',111,NULL,'19999999999',1,'Y','2026-06-09 15:32:01','2026-06-09 15:32:25'),(10,'18888888888','test','lueSGJZetyySpUndWjMBEg==',NULL,NULL,'18888888888',3,'Y','2026-06-22 18:46:06','2026-06-22 18:46:06');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_order_room_hotel`
--

DROP TABLE IF EXISTS `v_order_room_hotel`;
/*!50001 DROP VIEW IF EXISTS `v_order_room_hotel`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_order_room_hotel` AS SELECT 
 1 AS `id`,
 1 AS `customer_id`,
 1 AS `employee_id`,
 1 AS `room_id`,
 1 AS `check_in_date`,
 1 AS `check_out_date`,
 1 AS `total_amount`,
 1 AS `order_status`,
 1 AS `create_time`,
 1 AS `update_time`,
 1 AS `room_number`,
 1 AS `room_type`,
 1 AS `price`,
 1 AS `room_status`,
 1 AS `hotel_name`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_room_hotel`
--

DROP TABLE IF EXISTS `v_room_hotel`;
/*!50001 DROP VIEW IF EXISTS `v_room_hotel`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_room_hotel` AS SELECT 
 1 AS `id`,
 1 AS `hotel_id`,
 1 AS `room_number`,
 1 AS `room_type`,
 1 AS `price`,
 1 AS `room_status`,
 1 AS `create_time`,
 1 AS `hotel_name`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_order_room_hotel`
--

/*!50001 DROP VIEW IF EXISTS `v_order_room_hotel`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_order_room_hotel` AS select `o`.`id` AS `id`,`o`.`customer_id` AS `customer_id`,`o`.`employee_id` AS `employee_id`,`o`.`room_id` AS `room_id`,`o`.`check_in_date` AS `check_in_date`,`o`.`check_out_date` AS `check_out_date`,`o`.`total_amount` AS `total_amount`,`o`.`order_status` AS `order_status`,`o`.`create_time` AS `create_time`,`o`.`update_time` AS `update_time`,`r`.`room_number` AS `room_number`,`r`.`room_type` AS `room_type`,`r`.`price` AS `price`,`r`.`room_status` AS `room_status`,`h`.`hotel_name` AS `hotel_name` from ((`orders` `o` left join `room` `r` on((`o`.`room_id` = `r`.`id`))) left join `hotel` `h` on((`r`.`hotel_id` = `h`.`id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_room_hotel`
--

/*!50001 DROP VIEW IF EXISTS `v_room_hotel`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_room_hotel` AS select `r`.`id` AS `id`,`r`.`hotel_id` AS `hotel_id`,`r`.`room_number` AS `room_number`,`r`.`room_type` AS `room_type`,`r`.`price` AS `price`,`r`.`room_status` AS `room_status`,`r`.`create_time` AS `create_time`,`h`.`hotel_name` AS `hotel_name` from (`room` `r` left join `hotel` `h` on((`r`.`hotel_id` = `h`.`id`))) */;
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

-- Dump completed on 2026-06-23 14:02:06
