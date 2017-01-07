/*
Navicat MySQL Data Transfer

Source Server         : seckill
Source Server Version : 50528
Source Host           : localhost:3306
Source Database       : qq_robot

Target Server Type    : MYSQL
Target Server Version : 50528
File Encoding         : 65001

Date: 2017-01-06 15:50:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for robot_auto_reply
-- ----------------------------
DROP TABLE IF EXISTS `robot_auto_reply`;
CREATE TABLE `robot_auto_reply` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account` varchar(20) NOT NULL DEFAULT '' COMMENT 'QQ号',
  `is_auto_reply` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否自动回复',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `is_special` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否自定义回复列表',
  PRIMARY KEY (`id`),
  KEY `account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='自动回复列表';

-- ----------------------------
-- Table structure for robot_auto_reply_list
-- ----------------------------
DROP TABLE IF EXISTS `robot_auto_reply_list`;
CREATE TABLE `robot_auto_reply_list` (
  `pid` int(10) unsigned NOT NULL COMMENT '关联主表账号',
  `mark_name` varchar(100) NOT NULL DEFAULT '',
  `type` tinyint(1) NOT NULL COMMENT '0 私聊消息 1讨论组 2群',
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0：有效回复名单，1无效回复名单',
  UNIQUE KEY `pid_mark_name_unique` (`pid`,`mark_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='允许自动回复的昵称列表';

-- ----------------------------
-- Table structure for robot_user_info
-- ----------------------------
DROP TABLE IF EXISTS `robot_user_info`;
CREATE TABLE `robot_user_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `account` varchar(20) DEFAULT '' COMMENT 'QQ号',
  `gender` varchar(10) DEFAULT '' COMMENT '性别',
  `nick` varchar(50) DEFAULT '' COMMENT '昵称',
  `lnick` varchar(200) DEFAULT '' COMMENT '个性签名',
  `detail` text COMMENT '完整用户信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `quit_time` timestamp NULL DEFAULT NULL COMMENT '退出时间',
  PRIMARY KEY (`id`),
  KEY `account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8 COMMENT='用户信息表';
