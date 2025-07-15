CREATE DATABASE IF NOT EXISTS `palm-bank` DEFAULT CHARACTER SET utf8mb4;

USE `palm-bank`;

-- 用户表
CREATE TABLE `user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                        `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
                        `school_name` varchar(64) DEFAULT NULL COMMENT '学校',
                        `gender` tinyint(4) DEFAULT NULL COMMENT '性别',
                        `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
                        `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
                        `logo` varchar(256) DEFAULT NULL COMMENT 'logo图标',
                        `password` varchar(128) DEFAULT NULL COMMENT '密码',
                        `card_id` varchar(32) DEFAULT NULL COMMENT '银行卡号',
                        `balance` varchar(32) DEFAULT '0' COMMENT '余额',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_phone` (`phone`),
                        UNIQUE KEY `uk_card_id` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户token表
CREATE TABLE `user_token` (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                              `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                              `token` varchar(64) DEFAULT NULL COMMENT 'token',
                              `platform` tinyint(4) DEFAULT NULL COMMENT '登录平台',
                              `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                              PRIMARY KEY (`id`),
                              KEY `idx_token` (`token`),
                              KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录token表';