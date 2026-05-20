# 校园流浪动物管理系统数据库建表 SQL

## 1. 使用说明

本文档提供项目第一阶段可直接使用的 `MySQL 8.x` 建库建表脚本，适用于当前确定的技术方案：

- 前端：`Vue 3`
- 后端：`Spring Boot`
- 数据库：`MySQL`

脚本目标是先支持项目核心业务闭环，包括：

- 用户注册与登录
- 动物档案管理
- 动态广场
- 随手拍识别记录
- 个人中心基础数据查询

## 2. 建库脚本

```sql
CREATE DATABASE IF NOT EXISTS campus_animal_system
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE campus_animal_system;
```

## 3. 建表脚本

### 3.1 用户表 `sys_user`

```sql
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    nickname VARCHAR(50) NOT NULL COMMENT '用户昵称',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='用户表';
```

### 3.2 动物档案表 `animal`

```sql
DROP TABLE IF EXISTS animal;

CREATE TABLE animal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '动物ID',
    name VARCHAR(50) NOT NULL COMMENT '动物昵称',
    species VARCHAR(20) NOT NULL COMMENT '种类：猫/狗',
    gender VARCHAR(20) DEFAULT NULL COMMENT '性别',
    color VARCHAR(50) DEFAULT NULL COMMENT '毛色',
    location VARCHAR(100) NOT NULL COMMENT '常见地点',
    status VARCHAR(50) NOT NULL COMMENT '状态：已绝育/健康/待审核/待观察等',
    features VARCHAR(255) DEFAULT NULL COMMENT '特征标签，逗号分隔',
    image_url VARCHAR(255) DEFAULT NULL COMMENT '图片地址',
    sterilized TINYINT NOT NULL DEFAULT 0 COMMENT '是否绝育：1是 0否',
    vaccine_status VARCHAR(50) DEFAULT NULL COMMENT '疫苗状态',
    first_found_time DATETIME DEFAULT NULL COMMENT '首次发现时间',
    create_user_id BIGINT NOT NULL COMMENT '建档人ID',
    audit_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_animal_create_user FOREIGN KEY (create_user_id) REFERENCES sys_user(id)
) COMMENT='动物档案表';
```

### 3.3 动态表 `post`

```sql
DROP TABLE IF EXISTS post;

CREATE TABLE post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '动态ID',
    user_id BIGINT NOT NULL COMMENT '发布人ID',
    animal_id BIGINT DEFAULT NULL COMMENT '关联动物ID',
    type VARCHAR(20) NOT NULL COMMENT '类型：feed/rescue/checkin',
    content TEXT NOT NULL COMMENT '动态内容',
    image_url VARCHAR(255) DEFAULT NULL COMMENT '动态配图',
    location VARCHAR(100) DEFAULT NULL COMMENT '发布位置',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_post_animal FOREIGN KEY (animal_id) REFERENCES animal(id)
) COMMENT='动态表';
```

### 3.4 扫描记录表 `scan_record`

```sql
DROP TABLE IF EXISTS scan_record;

CREATE TABLE scan_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '扫描记录ID',
    user_id BIGINT NOT NULL COMMENT '上传用户ID',
    image_url VARCHAR(255) NOT NULL COMMENT '上传图片地址',
    result_type VARCHAR(20) NOT NULL COMMENT '识别结果：matched/new',
    matched_animal_id BIGINT DEFAULT NULL COMMENT '匹配到的动物ID',
    similarity DECIMAL(5,2) DEFAULT NULL COMMENT '匹配度，例如92.50',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '识别时间',
    CONSTRAINT fk_scan_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_scan_animal FOREIGN KEY (matched_animal_id) REFERENCES animal(id)
) COMMENT='扫描记录表';
```

### 3.5 领养申请表 `adoption_application`

```sql
DROP TABLE IF EXISTS adoption_application;

CREATE TABLE adoption_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    animal_id BIGINT NOT NULL COMMENT '动物ID',
    reason VARCHAR(255) DEFAULT NULL COMMENT '申请理由',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/APPROVED/REJECTED',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_adoption_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
    CONSTRAINT fk_adoption_animal FOREIGN KEY (animal_id) REFERENCES animal(id)
) COMMENT='领养申请表';
```

### 3.6 通知表 `notification`

```sql
DROP TABLE IF EXISTS notification;

CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '接收用户ID',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content VARCHAR(255) NOT NULL COMMENT '通知内容',
    is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：1是 0否',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '通知时间',
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) COMMENT='通知表';
```

## 4. 初始化测试数据

### 4.1 初始化用户

说明：
下面密码字段示例值仅用于初始化演示，正式开发时应使用后端加密后再写入数据库。

```sql
INSERT INTO sys_user (username, password, nickname, role, avatar)
VALUES
('admin', '123456', '管理员', 'ADMIN', NULL),
('student01', '123456', '李同学', 'USER', NULL),
('student02', '123456', '张同学', 'USER', NULL);
```

### 4.2 初始化动物档案

```sql
INSERT INTO animal
(name, species, gender, color, location, status, features, image_url, sterilized, vaccine_status, first_found_time, create_user_id, audit_status)
VALUES
('大橘', '猫', '未知', '橘色', '北区食堂', '已绝育', '亲人,贪吃', '/uploads/cat1.png', 1, '未知', NOW(), 2, 'APPROVED'),
('学霸狗', '狗', '未知', '黄色', '图书馆门口', '健康', '安静,怕生', '/uploads/dog1.png', 0, '未知', NOW(), 2, 'APPROVED'),
('三花', '猫', '未知', '三花', '南门草坪', '未绝育', '警惕,爱晒太阳', '/uploads/cat1.png', 0, '未知', NOW(), 3, 'APPROVED'),
('小黄', '狗', '未知', '黄色', '体育馆侧门', '待观察', '活泼,亲人', '/uploads/dog2.png', 0, '未知', NOW(), 3, 'PENDING');
```

### 4.3 初始化动态

```sql
INSERT INTO post
(user_id, animal_id, type, content, image_url, location)
VALUES
(2, 1, 'feed', '在北区食堂打卡投喂大橘，状态活跃，亲人。', '/uploads/cat1.png', '北区食堂'),
(3, 3, 'checkin', '南门草坪已补充猫粮和清水。', NULL, '南门草坪'),
(2, NULL, 'rescue', '教学楼B区发现疑似受伤幼猫，正在联系救助。', NULL, '教学楼B区');
```

## 5. 索引建议

为提高查询效率，建议补充以下索引：

```sql
CREATE INDEX idx_animal_species ON animal(species);
CREATE INDEX idx_animal_location ON animal(location);
CREATE INDEX idx_animal_status ON animal(status);
CREATE INDEX idx_post_type ON post(type);
CREATE INDEX idx_post_create_time ON post(create_time);
CREATE INDEX idx_scan_result_type ON scan_record(result_type);
```

## 6. 开发建议

- 后端实体类字段命名建议和表字段保持一致，便于 `MyBatis-Plus` 映射。
- 用户密码不要明文存储，正式版本应使用 `BCryptPasswordEncoder`。
- 图片上传成功后，只在数据库里保存图片访问路径，不保存二进制内容。
- 当前表结构已经足够支撑第一版答辩演示，后续如需补充地图轨迹、志愿服务时长等能力，可继续扩表。
