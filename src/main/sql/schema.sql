CREATE TABLE `robot_user_info` (
`id`  int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键' ,
`account`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT 'QQ号' ,
`gender`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '性别' ,
`nick`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '昵称' ,
`lnick`  varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '个性签名' ,
`detail`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '完整用户信息' ,
`createTime`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' ,
PRIMARY KEY (`id`),
INDEX (`account`) 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
COMMENT='用户信息表'
AUTO_INCREMENT=1
ROW_FORMAT=COMPACT
;

