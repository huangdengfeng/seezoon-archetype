CREATE TABLE `sys_user` (
  `uid` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名 ',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `secret_key` varchar(32) NOT NULL COMMENT '安全密钥',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `photo` varchar(100) DEFAULT NULL COMMENT '照片',
  `status` tinyint NOT NULL COMMENT '状态：1.正常;2.停用;3.锁定',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `create_user` int NOT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `update_user` int NOT NULL COMMENT '更新用户',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `uidx_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_name` (`name`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$hTbIfacFP2Vl0EX.qmQBB.WI/zESwGc2WiMOnykfBzOoiiKIGSGCa','NYivApKMepQQLYul5xGegsUQA440ox4h','管理员',NULL,NULL,NULL,1,'2023-08-27 09:16:23',1,'2023-10-20 22:55:18',1,NULL);


CREATE TABLE `sys_user_role` (
  `uid` int NOT NULL COMMENT '用户ID',
  `role` varchar(50) NOT NULL COMMENT '角色代码',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`uid`, `role`),
  KEY `idx_uid` (`uid`),
  KEY `idx_role` (`role`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';


CREATE TABLE `sys_session` (
  `session_id` varchar(36) NOT NULL COMMENT '会话ID',
  `uid` int NOT NULL COMMENT '用户ID',
  `max_inactive_interval` INT NOT NULL COMMENT '最长不活跃时间（S）',
  `expire_time` datetime NOT NULL,
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `last_access_time` datetime NOT NULL COMMENT '最后访问时间',
  `data` text NOT NULL COMMENT '会话数据',
  PRIMARY KEY (`session_id`),
  KEY `idx_uid` (`uid`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
