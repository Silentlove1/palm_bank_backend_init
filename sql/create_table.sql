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

-- 交易记录表
CREATE TABLE `trade_record` (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                                `trade_id` varchar(64) DEFAULT NULL COMMENT '交易id（唯一）',
                                `trade_type` tinyint(4) DEFAULT NULL COMMENT '交易类型：1-充值，2-提现，3-转账收入，4-转账支出',
                                `trade_amount` varchar(32) DEFAULT NULL COMMENT '交易金额',
                                `trade_balance` varchar(32) DEFAULT NULL COMMENT '交易后余额',
                                `trade_channel` tinyint(4) DEFAULT NULL COMMENT '交易渠道：1-支付宝，2-微信，3-云闪付，4-银行转账',
                                `trade_time` datetime DEFAULT NULL COMMENT '交易时间',
                                `trade_status` tinyint(4) DEFAULT NULL COMMENT '交易状态：1-处理中，2-成功，3-失败',
                                `trade_desc` varchar(256) DEFAULT NULL COMMENT '交易描述',
                                `target_user_id` int(11) DEFAULT NULL COMMENT '目标用户id（转账时使用）',
                                `target_card_id` varchar(32) DEFAULT NULL COMMENT '目标银行卡号（转账时使用）',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_trade_id` (`trade_id`),
                                KEY `idx_user_id` (`user_id`),
                                KEY `idx_trade_time` (`trade_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- 交易防重表（防止重复提交）
CREATE TABLE `trade_token` (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                               `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                               `token` varchar(64) DEFAULT NULL COMMENT 'token',
                               `trade_type` varchar(32) DEFAULT NULL COMMENT '交易类型',
                               `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_token` (`token`),
                               KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易防重表';

-- 对账统计表
CREATE TABLE `trade_board` (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                               `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                               `date_type` tinyint(4) DEFAULT NULL COMMENT '日期类型：1-日，2-月，3-总',
                               `date_str` varchar(32) DEFAULT NULL COMMENT '日期字符串',
                               `total_income` varchar(32) DEFAULT '0' COMMENT '总收入',
                               `total_expense` varchar(32) DEFAULT '0' COMMENT '总支出',
                               `recharge_amount` varchar(32) DEFAULT '0' COMMENT '充值金额',
                               `withdraw_amount` varchar(32) DEFAULT '0' COMMENT '提现金额',
                               `transfer_in_amount` varchar(32) DEFAULT '0' COMMENT '转入金额',
                               `transfer_out_amount` varchar(32) DEFAULT '0' COMMENT '转出金额',
                               `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_user_date` (`user_id`,`date_type`,`date_str`),
                               KEY `idx_user_id` (`user_id`),
                               KEY `idx_date_str` (`date_str`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对账统计表';

-- 帖子表
CREATE TABLE `post` (
                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                        `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                        `title` varchar(256) DEFAULT NULL COMMENT '标题',
                        `content` text COMMENT '内容',
                        `view_count` int(11) DEFAULT '0' COMMENT '浏览量',
                        `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
                        `comment_count` int(11) DEFAULT '0' COMMENT '评论数',
                        `collect_count` int(11) DEFAULT '0' COMMENT '收藏数',
                        `status` tinyint(4) DEFAULT '1' COMMENT '状态：0-草稿，1-已发布，2-已删除',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                        PRIMARY KEY (`id`),
                        KEY `idx_user_id` (`user_id`),
                        KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 评论表
CREATE TABLE `comment` (
                           `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                           `post_id` int(11) DEFAULT NULL COMMENT '帖子id',
                           `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                           `parent_id` int(11) DEFAULT '0' COMMENT '父评论id，0表示一级评论',
                           `content` varchar(1024) DEFAULT NULL COMMENT '评论内容',
                           `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
                           `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                           PRIMARY KEY (`id`),
                           KEY `idx_post_id` (`post_id`),
                           KEY `idx_user_id` (`user_id`),
                           KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 点赞记录表
CREATE TABLE `user_like` (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                             `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                             `target_id` int(11) DEFAULT NULL COMMENT '目标id（帖子id或评论id）',
                             `target_type` tinyint(4) DEFAULT NULL COMMENT '目标类型：1-帖子，2-评论',
                             `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uk_user_target` (`user_id`,`target_id`,`target_type`),
                             KEY `idx_target` (`target_id`,`target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

-- 收藏表
CREATE TABLE `user_collect` (
                                `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                                `post_id` int(11) DEFAULT NULL COMMENT '帖子id',
                                `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_user_post` (`user_id`,`post_id`),
                                KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 帖子表
CREATE TABLE `post` (
                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                        `user_id` int(11) DEFAULT NULL COMMENT '用户id',
                        `title` varchar(256) DEFAULT NULL COMMENT '标题',
                        `content` text COMMENT '内容',
                        `view_count` int(11) DEFAULT '0' COMMENT '浏览量',
                        `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
                        `comment_count` int(11) DEFAULT '0' COMMENT '评论数',
                        `collect_count` int(11) DEFAULT '0' COMMENT '收藏数',
                        `status` tinyint(4) DEFAULT '1' COMMENT '状态：0-草稿，1-已发布，2-已删除',
                        `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        `is_delete` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
                        PRIMARY KEY (`id`),
                        KEY `idx_user_id` (`user_id`),
                        KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';
