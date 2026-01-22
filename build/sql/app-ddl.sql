CREATE SCHEMA `seezoon` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
use `seezoon`;


CREATE TABLE `user_info`
(
    `uid`         bigint      NOT NULL AUTO_INCREMENT,
    `username`    varchar(100) DEFAULT NULL COMMENT '用户名',
    `password`    varchar(100) DEFAULT NULL COMMENT '密码',
    `secret_key`  varchar(32) NOT NULL COMMENT '用户安全Key',
    `status`      tinyint     NOT NULL COMMENT '状态1.有效;2.无效;3.锁定',
    `create_time` datetime    NOT NULL,
    `update_time` datetime    NOT NULL,
    PRIMARY KEY (`uid`),
    UNIQUE KEY `uidx_username` (`username`)
) ENGINE = InnoDB AUTO_INCREMENT = 1000000 DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT='用户信息';

CREATE TABLE `user_oauth`
(
    `uid`         bigint       NOT NULL,
    `oauth_type`  tinyint      NOT NULL COMMENT 'auth类型1.微信app;2.小程序;3.公众号',
    `oauth_id`    varchar(100) NOT NULL,
    `union_id`    varchar(100) DEFAULT NULL,
    `create_time` datetime     NOT NULL,
    `update_time` datetime     NOT NULL,
    PRIMARY KEY (`uid`, `oauth_type`, `oauth_id`),
    UNIQUE KEY `idx_t_oauth_oauth_type_oauth_id` (`oauth_type`, `oauth_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT='三方登录';

CREATE TABLE `user_profile`
(
    `uid`         bigint   NOT NULL,
    `nick_name`   varchar(100) DEFAULT NULL COMMENT '昵称',
    `name`        varchar(100) DEFAULT NULL COMMENT '姓名',
    `mobile`      varchar(45)  DEFAULT NULL COMMENT '手机号',
    `avatar`      varchar(100) DEFAULT NULL COMMENT '头像',
    `email`       varchar(100) DEFAULT NULL COMMENT '邮箱',
    `birthday`    date         DEFAULT NULL COMMENT '生日',
    `address`     varchar(100) DEFAULT NULL COMMENT '地址',
    `create_time` datetime NOT NULL COMMENT '创建时间',
    `update_time` datetime NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`uid`),
    UNIQUE KEY `uidx_mobile` (`mobile`),
    UNIQUE KEY `uidx_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT='用户资料';


CREATE TABLE `user_refresh_token`
(
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uid`         bigint      NOT NULL COMMENT '用户ID',
    `client_id`    varchar(100) NOT NULL COMMENT '终端Id',
    `refresh_token_id`  varchar(64) NOT NULL COMMENT 'refresh token id',
    `token_generation` INT NOT NULL COMMENT '令牌代数',
    `replaced_time` DATETIME NULL COMMENT '被替换时间',
    `grace_period_end` DATETIME NULL COMMENT '宽限期结束时间',
    `status`     tinyint     NOT NULL COMMENT '状态1.有效;2.被替换;3.失效',
    `issue_time` datetime    NOT NULL  COMMENT '颁发时间',
    `expire_time` datetime   NOT NULL COMMENT '过期时间',
    `update_time` datetime   NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_user_client` (`uid`, `client_id`),
    UNIQUE KEY `uidx_refresh_token_id` (`refresh_token_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT='用户登录态刷新token';


CREATE TABLE `sys_file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件唯一ID（主键）',
  `name` VARCHAR(255) NOT NULL COMMENT '文件名',
  `mime_type` VARCHAR(100) NOT NULL COMMENT 'MIME类型（如 "image/jpeg", "image/png"）',
  `file_size` INT NOT NULL COMMENT '图片原始大小（字节）',
  `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `data` MEDIUMBLOB NOT NULL COMMENT '二进制数据（MEDIUMBLOB支持最大16MB）',
   `uid` bigint      DEFAULT NULL COMMENT '用户ID',
  `create_time` datetime   NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_uid` (`uid`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT='存储图片二进制数据的表';

CREATE TABLE `sys_param` (
  `param_key` varchar(128) NOT NULL COMMENT '参数KEY',
  `param_name` varchar(128) NOT NULL COMMENT '参数名称',
  `param_value` text NOT NULL COMMENT '参数值',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`param_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数表';

CREATE TABLE `sys_security` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键ID，唯一标识一条操作记录',
  `uid` BIGINT NOT NULL COMMENT 'uid',
  `operation` int NOT NULL COMMENT '操作类型',
  `data` varchar(128) DEFAULT NULL COMMENT '操作相关的详细数据',
  `create_time` datetime   NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_uid` (`uid`),
  KEY `idx_operation` (`operation`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT '用户安全操作记录表（用于审计、追溯敏感操作）';

