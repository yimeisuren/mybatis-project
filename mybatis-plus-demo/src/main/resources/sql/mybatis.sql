USE `mybatis`;
# 创建表
CREATE TABLE IF NOT EXISTS `user`
(
    id          BIGINT(20)  NOT NULL COMMENT '主键ID',
    name        VARCHAR(30) NULL     DEFAULT NULL COMMENT '姓名',
    age         INT(11)     NULL     DEFAULT NULL COMMENT '年龄',
    email       VARCHAR(50) NULL     DEFAULT NULL COMMENT '邮箱',
    is_delete   BOOLEAN     NOT NULL DEFAULT FALSE COMMENT '逻辑删除标志',
    create_time DATETIME    NULL     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME    NULL     DEFAULT NULL COMMENT '更新时间',
    version     INT         NULL     DEFAULT NULL COMMENT '版本号',
    PRIMARY KEY (id)
);

# 添加数据
INSERT INTO `user`(ID, NAME, AGE, EMAIL)
VALUES (1, 'Jone', 18, 'Jone@qq.com'),
       (2, 'Jack', 20, 'Jack@qq.com'),
       (3, 'Tom', 28, 'Tom@qq.com'),
       (4, 'Sandy', 21, 'Sandy@qq.com'),
       (5, 'Billie', 24, 'Billie@qq.com');
