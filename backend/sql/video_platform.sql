-- =============================================
-- 视频平台数据库表结构设计
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS video_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE video_platform;

-- =============================================
-- 用户相关表
-- =============================================

-- 用户表
CREATE TABLE `user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `avatar` VARCHAR(500) COMMENT '头像URL',
  `bio` TEXT COMMENT '个人简介',
  `gender` TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
  `birthday` DATE COMMENT '生日',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
  `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色: user-普通用户, admin-管理员',
  `follower_count` INT DEFAULT 0 COMMENT '粉丝数',
  `following_count` INT DEFAULT 0 COMMENT '关注数',
  `like_count` BIGINT DEFAULT 0 COMMENT '获赞总数',
  `video_count` INT DEFAULT 0 COMMENT '作品数',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户关注表
CREATE TABLE `user_follow` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `follow_user_id` BIGINT UNSIGNED NOT NULL COMMENT '被关注用户ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follow` (`user_id`, `follow_user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_follow_user_id` (`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- =============================================
-- 视频相关表
-- =============================================

-- 视频表
CREATE TABLE `video` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '视频ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '发布用户ID',
  `title` VARCHAR(200) NOT NULL COMMENT '视频标题',
  `description` TEXT COMMENT '视频描述',
  `cover_url` VARCHAR(500) COMMENT '封面URL',
  `video_url` VARCHAR(500) NOT NULL COMMENT '视频URL',
  `duration` INT COMMENT '视频时长(秒)',
  `width` INT COMMENT '视频宽度',
  `height` INT COMMENT '视频高度',
  `size` BIGINT COMMENT '文件大小(字节)',
  `category_id` INT COMMENT '分类ID',
  `status` TINYINT DEFAULT 0 COMMENT '状态: 0-待审核, 1-已发布, 2-审核失败, 3-已下架',
  `is_private` TINYINT DEFAULT 0 COMMENT '是否私密: 0-公开, 1-私密',
  `view_count` BIGINT DEFAULT 0 COMMENT '播放量',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `share_count` INT DEFAULT 0 COMMENT '分享数',
  `favorite_count` INT DEFAULT 0 COMMENT '收藏数',
  `published_at` DATETIME COMMENT '发布时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_published_at` (`published_at`),
  KEY `idx_view_count` (`view_count`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频表';

-- 视频标签表
CREATE TABLE `video_tag` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `video_count` INT DEFAULT 0 COMMENT '视频数量',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频标签表';

-- 视频标签关联表
CREATE TABLE `video_tag_relation` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `video_id` BIGINT UNSIGNED NOT NULL COMMENT '视频ID',
  `tag_id` INT UNSIGNED NOT NULL COMMENT '标签ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_video_tag` (`video_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频标签关联表';

-- 视频分类表
CREATE TABLE `video_category` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `parent_id` INT DEFAULT 0 COMMENT '父分类ID',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `icon` VARCHAR(200) COMMENT '图标',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频分类表';

-- =============================================
-- 互动相关表
-- =============================================

-- 点赞表
CREATE TABLE `user_like` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `video_id` BIGINT UNSIGNED NOT NULL COMMENT '视频ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
  KEY `idx_video_id` (`video_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞表';

-- 收藏表
CREATE TABLE `user_favorite` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `video_id` BIGINT UNSIGNED NOT NULL COMMENT '视频ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
  KEY `idx_video_id` (`video_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 评论表
CREATE TABLE `comment` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `video_id` BIGINT UNSIGNED NOT NULL COMMENT '视频ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '评论用户ID',
  `parent_id` BIGINT UNSIGNED DEFAULT 0 COMMENT '父评论ID(0为顶级评论)',
  `reply_user_id` BIGINT UNSIGNED COMMENT '回复的用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-已删除, 1-正常',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_video_id` (`video_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- =============================================
-- 历史记录表
-- =============================================

-- 观看历史表
CREATE TABLE `user_history` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `video_id` BIGINT UNSIGNED NOT NULL COMMENT '视频ID',
  `watch_duration` INT DEFAULT 0 COMMENT '观看时长(秒)',
  `watch_progress` DECIMAL(5,2) DEFAULT 0 COMMENT '观看进度(百分比)',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '观看时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户观看历史表';

-- =============================================
-- 消息相关表
-- =============================================

-- 消息表
CREATE TABLE `message` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `from_user_id` BIGINT UNSIGNED COMMENT '发送者ID(系统消息为null)',
  `to_user_id` BIGINT UNSIGNED NOT NULL COMMENT '接收者ID',
  `type` TINYINT NOT NULL COMMENT '消息类型: 1-系统消息, 2-点赞, 3-评论, 4-关注, 5-私信',
  `content` TEXT COMMENT '消息内容',
  `related_id` BIGINT UNSIGNED COMMENT '关联ID(视频ID/评论ID等)',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_to_user_id` (`to_user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- =============================================
-- 系统管理表
-- =============================================

-- 系统配置表
CREATE TABLE `sys_config` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `description` VARCHAR(500) COMMENT '描述',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 审核记录表
CREATE TABLE `audit_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `video_id` BIGINT UNSIGNED NOT NULL COMMENT '视频ID',
  `auditor_id` BIGINT UNSIGNED NOT NULL COMMENT '审核员ID',
  `status` TINYINT NOT NULL COMMENT '审核结果: 1-通过, 2-拒绝',
  `reason` TEXT COMMENT '审核意见',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  PRIMARY KEY (`id`),
  KEY `idx_video_id` (`video_id`),
  KEY `idx_auditor_id` (`auditor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- 操作日志表
CREATE TABLE `operation_log` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT UNSIGNED COMMENT '操作用户ID',
  `module` VARCHAR(50) COMMENT '模块名称',
  `operation` VARCHAR(100) COMMENT '操作类型',
  `method` VARCHAR(200) COMMENT '请求方法',
  `params` TEXT COMMENT '请求参数',
  `ip` VARCHAR(50) COMMENT 'IP地址',
  `location` VARCHAR(200) COMMENT 'IP归属地',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-失败, 1-成功',
  `error_msg` TEXT COMMENT '错误信息',
  `cost_time` INT COMMENT '耗时(ms)',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =============================================
-- 初始化数据
-- =============================================

-- 插入默认管理员
INSERT INTO `user` (`username`, `nickname`, `email`, `password`, `role`, `avatar`, `bio`) 
VALUES ('admin', '管理员', 'admin@videoplatform.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lPk5z8YQZx1dZ77i2', 'admin', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', '平台管理员');

-- 插入默认分类
INSERT INTO `video_category` (`name`, `parent_id`, `sort`, `icon`) VALUES
('生活', 0, 1, '🏠'),
('娱乐', 0, 2, '🎬'),
('游戏', 0, 3, '🎮'),
('科技', 0, 4, '💻'),
('音乐', 0, 5, '🎵'),
('美食', 0, 6, '🍔'),
('运动', 0, 7, '⚽'),
('教育', 0, 8, '📚');

-- 插入默认标签
INSERT INTO `video_tag` (`name`, `video_count`) VALUES
('vlog', 25), ('搞笑', 42), ('美食', 38), ('旅游', 31), ('教程', 29),
('游戏解说', 56), ('音乐', 45), ('舞蹈', 18), ('运动', 22), ('科技', 33);

-- 插入视频标签关联数据
INSERT INTO `video_tag_relation` (`video_id`, `tag_id`) VALUES
(1, 1), (1, 4),  -- 周末城市漫步VLOG: vlog, 旅游
(2, 5), (2, 10), -- Java编程入门教程: 教程, 科技
(3, 3), (3, 5),  -- 法式甜品制作指南: 美食, 教程
(4, 4), (4, 1),  -- 海岛度假旅行日记: 旅游, vlog
(5, 6), (5, 2),  -- 英雄联盟排位上分攻略: 游戏解说, 搞笑
(6, 9), (6, 5),  -- 家庭健身训练计划: 运动, 教程
(7, 10), (7, 5), -- Python数据分析实战: 科技, 教程
(8, 3), (8, 5);  -- 日式拉面制作秘籍: 美食, 教程

-- 更新标签的视频数量统计
UPDATE `video_tag` SET `video_count` = `video_count` + 2 WHERE `id` = 1;  -- vlog
UPDATE `video_tag` SET `video_count` = `video_count` + 2 WHERE `id` = 2;  -- 搞笑
UPDATE `video_tag` SET `video_count` = `video_count` + 4 WHERE `id` = 3;  -- 美食
UPDATE `video_tag` SET `video_count` = `video_count` + 2 WHERE `id` = 4;  -- 旅游
UPDATE `video_tag` SET `video_count` = `video_count` + 6 WHERE `id` = 5;  -- 教程
UPDATE `video_tag` SET `video_count` = `video_count` + 2 WHERE `id` = 6;  -- 游戏解说
UPDATE `video_tag` SET `video_count` = `video_count` + 0 WHERE `id` = 7;  -- 音乐
UPDATE `video_tag` SET `video_count` = `video_count` + 0 WHERE `id` = 8;  -- 舞蹈
UPDATE `video_tag` SET `video_count` = `video_count` + 2 WHERE `id` = 9;  -- 运动
UPDATE `video_tag` SET `video_count` = `video_count` + 2 WHERE `id` = 10; -- 科技

-- 插入测试视频
INSERT INTO `video` (`user_id`, `title`, `description`, `cover_url`, `video_url`, `duration`, `width`, `height`, `size`, `category_id`, `status`, `view_count`, `like_count`, `comment_count`, `share_count`, `favorite_count`, `published_at`) VALUES
(2, '周末城市漫步VLOG', '记录周末在城市中的悠闲时光', 'https://picsum.photos/800/450?random=1', 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 320, 1920, 1080, 1024000, 1, 1, 1200, 45, 12, 8, 32, '2025-11-20 10:00:00'),
(3, 'Java编程入门教程', '从零开始学习Java编程语言', 'https://picsum.photos/800/450?random=2', 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 850, 1920, 1080, 2048000, 4, 1, 2500, 89, 24, 15, 67, '2025-11-18 14:30:00'),
(4, '法式甜品制作指南', '教你制作正宗的法式马卡龙', 'https://picsum.photos/800/450?random=3', 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 620, 1920, 1080, 1800000, 6, 1, 3200, 120, 38, 22, 89, '2025-11-15 09:15:00'),
(5, '海岛度假旅行日记', '记录在马尔代夫的绝美时光', 'https://picsum.photos/800/450?random=4', 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 540, 1920, 1080, 1500000, 1, 1, 4500, 210, 45, 33, 156, '2025-11-10 16:45:00'),
(6, '英雄联盟排位上分攻略', '职业选手分享的上分技巧', 'https://picsum.photos/800/450?random=5', 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 920, 1920, 1080, 2200000, 3, 1, 5800, 320, 67, 45, 234, '2025-11-05 20:00:00'),
(2, '家庭健身训练计划', '无需器械的居家锻炼方法', 'https://picsum.photos/800/450?random=6', 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 420, 1920, 1080, 1200000, 7, 1, 1800, 76, 18, 12, 45, '2025-11-01 08:30:00'),
(3, 'Python数据分析实战', '使用Python进行数据可视化', 'https://picsum.photos/800/450?random=7', 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 780, 1920, 1080, 1980000, 4, 1, 2100, 92, 28, 18, 78, '2025-10-28 13:20:00'),
(4, '日式拉面制作秘籍', '正宗豚骨拉面的家庭做法', 'https://picsum.photos/800/450?random=8', 'https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4', 680, 1920, 1080, 1720000, 6, 1, 2900, 134, 41, 26, 98, '2025-10-25 11:45:00');

-- 插入用户关注关系
INSERT INTO `user_follow` (`user_id`, `follow_user_id`) VALUES
(2, 3), (2, 4), (2, 5), (2, 6),
(3, 2), (3, 4), (3, 5),
(4, 2), (4, 3), (4, 6),
(5, 2), (5, 3), (5, 4), (5, 6),
(6, 2), (6, 3), (6, 4), (6, 5);

-- 更新用户的关注数和粉丝数
UPDATE `user` SET `following_count` = 4, `follower_count` = 4 WHERE `id` = 2;
UPDATE `user` SET `following_count` = 3, `follower_count` = 4 WHERE `id` = 3;
UPDATE `user` SET `following_count` = 3, `follower_count` = 4 WHERE `id` = 4;
UPDATE `user` SET `following_count` = 4, `follower_count` = 4 WHERE `id` = 5;
UPDATE `user` SET `following_count` = 4, `follower_count` = 4 WHERE `id` = 6;

-- 插入点赞数据
INSERT INTO `user_like` (`user_id`, `video_id`) VALUES
(2, 3), (2, 4), (2, 5),
(3, 1), (3, 4), (3, 5), (3, 6),
(4, 1), (4, 2), (4, 5), (4, 6),
(5, 1), (5, 2), (5, 3), (5, 6),
(6, 1), (6, 2), (6, 3), (6, 4);

-- 更新视频的点赞数
UPDATE `video` SET `like_count` = `like_count` + 4 WHERE `id` = 1;
UPDATE `video` SET `like_count` = `like_count` + 3 WHERE `id` = 2;
UPDATE `video` SET `like_count` = `like_count` + 3 WHERE `id` = 3;
UPDATE `video` SET `like_count` = `like_count` + 4 WHERE `id` = 4;
UPDATE `video` SET `like_count` = `like_count` + 4 WHERE `id` = 5;
UPDATE `video` SET `like_count` = `like_count` + 4 WHERE `id` = 6;

-- 插入收藏数据
INSERT INTO `user_favorite` (`user_id`, `video_id`) VALUES
(2, 4), (2, 5),
(3, 1), (3, 5), (3, 6),
(4, 1), (4, 2), (4, 6),
(5, 1), (5, 2), (5, 3),
(6, 2), (6, 3), (6, 4);

-- 更新视频的收藏数
UPDATE `video` SET `favorite_count` = `favorite_count` + 3 WHERE `id` = 1;
UPDATE `video` SET `favorite_count` = `favorite_count` + 3 WHERE `id` = 2;
UPDATE `video` SET `favorite_count` = `favorite_count` + 2 WHERE `id` = 3;
UPDATE `video` SET `favorite_count` = `favorite_count` + 3 WHERE `id` = 4;
UPDATE `video` SET `favorite_count` = `favorite_count` + 3 WHERE `id` = 5;
UPDATE `video` SET `favorite_count` = `favorite_count` + 2 WHERE `id` = 6;

-- 插入评论数据
INSERT INTO `comment` (`video_id`, `user_id`, `content`, `like_count`) VALUES
(1, 3, '很棒的vlog，很有生活气息！', 5),
(1, 4, '拍摄角度很好，学到了', 3),
(2, 2, '讲解很详细，适合初学者', 8),
(2, 5, '这个知识点讲得很透彻', 6),
(3, 2, '看起来很好吃，想尝试一下', 12),
(3, 6, '步骤很清晰，感谢分享', 7),
(4, 2, '风景太美了，向往这样的旅行', 15),
(4, 3, '摄影技术真棒！', 9),
(5, 2, '学到了很多新技巧', 18),
(5, 4, '大神的操作就是不一样', 14);

-- 更新视频的评论数
UPDATE `video` SET `comment_count` = `comment_count` + 2 WHERE `id` = 1;
UPDATE `video` SET `comment_count` = `comment_count` + 2 WHERE `id` = 2;
UPDATE `video` SET `comment_count` = `comment_count` + 2 WHERE `id` = 3;
UPDATE `video` SET `comment_count` = `comment_count` + 2 WHERE `id` = 4;
UPDATE `video` SET `comment_count` = `comment_count` + 2 WHERE `id` = 5;
UPDATE `video` SET `comment_count` = `comment_count` + 0 WHERE `id` = 6;

-- 插入观看历史数据
INSERT INTO `user_history` (`user_id`, `video_id`, `watch_duration`, `watch_progress`) VALUES
(2, 1, 280, 87.5),
(2, 3, 520, 83.9),
(2, 5, 750, 81.5),
(3, 2, 780, 91.8),
(3, 4, 480, 88.9),
(4, 1, 320, 100.0),
(4, 6, 850, 92.4),
(5, 2, 420, 100.0),
(5, 3, 620, 100.0),
(6, 4, 540, 100.0);




