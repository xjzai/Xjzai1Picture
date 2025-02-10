# Xjzai1Picture

## 用户模块

**一、需求分析**

		- 用户注册 
		- 用户登录
		- 获取当前用户信息
		- 用户注销
		- 用户权限控制
		- 【管理员】 管理用户
		- 用户更新

**二、方案设计**

- **库表设计**

  库名：yu_picture

  表名：user（用户表）

  #### 1、核心设计

  用户表的核心是用户登录凭证（账号密码）和个人信息，SQL 如下：

  ```sql
  ▼sql复制代码-- 用户表
  create table if not exists user
  (
      id           bigint auto_increment comment 'id' primary key,
      user_account  varchar(256)                           not null comment '账号',
      user_password varchar(512)                           not null comment '密码',
      user_name     varchar(256)                           null comment '用户昵称',
      user_avatar   varchar(1024)                          null comment '用户头像',
      user_profile  varchar(512)                           null comment '用户简介',
      user_role     varchar(256) default 'user'            not null comment '用户角色：user/admin',
      edit_time     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
      create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
      update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
      is_delete     tinyint      default 0                 not null comment '是否删除',
      UNIQUE KEY uk_user_account (user_account),
      INDEX idx_user_name (user_name)
  ) comment '用户' collate = utf8mb4_unicode_ci;
  ```

  几个注意事项：

  1）editTime 和 updateTime 的区别：editTime 表示用户编辑个人信息的时间（需要业务代码来更新），而 updateTime 表示这条用户记录任何字段发生修改的时间（由数据库自动更新）。

  2）给唯一值添加唯一键（唯一索引），比如账号 userAccount，利用数据库天然防重复，同时可以增加查询效率。l+ls5gH5aUQrdxNgINThOmltTg6si9vAukovcRsU7uo=

  3）给经常用于查询的字段添加索引，比如用户昵称 userName，可以增加查询效率。

  💡 建议养成好习惯，将库表设计 SQL 保存到项目的目录中，比如新建 `sql/create_table.sql` 文件，这样其他开发者就能更快地了解项目。

  #### 2、扩展设计

  1）如果要实现会员功能，可以对表进行如下扩展：

  1. 给 userRole 字段新增枚举值 `vip`，表示会员用户，可根据该值判断用户权限
  2. 新增会员过期时间字段，可用于记录会员有效期
  3. 新增会员兑换码字段，可用于记录会员的开通方式
  4. 新增会员编号字段，可便于定位用户并提供额外服务，并增加会员归属感

  对应的 SQL 如下：ASMIm692hddgvLuS8K2U2nkkHTASdtzJju0lxnxR+D8=

  ```sql
  ▼sql复制代码vipExpireTime datetime     null comment '会员过期时间',
  vipCode       varchar(128) null comment '会员兑换码',
  vipNumber     bigint       null comment '会员编号'
  ```

  2）如果要实现用户邀请功能，可以对表进行如下扩展：

  1. 新增 shareCode 分享码字段，用于记录每个用户的唯一邀请标识，可拼接到邀请网址后面，比如 https://mianshiya.com/?shareCode=xxx
  2. 新增 inviteUser 字段，用于记录该用户被哪个用户邀请了，可通过这个字段查询某用户邀请的用户列表。

  对应的 SQL 如下：

  ```sql
  ▼sql复制代码shareCode     varchar(20)  DEFAULT NULL COMMENT '分享码',
  inviteUser    bigint       DEFAULT NULL COMMENT '邀请用户 id'
  ```

- 用户登录流程

  使用cookie + session

- 如何对用户权限进行控制

  可以将接口分为 4 种权限：ASMIm692hddgvLuS8K2U2nkkHTASdtzJju0lxnxR+D8=

  1. 未登录也可以使用
  2. 登录用户才能使用
  3. 未登录也可以使用，但是登录用户能进行更多操作（比如登录后查看全文）
  4. 仅管理员才能使用

  传统的权限控制方法是，在每个接口内单独编写逻辑：先获取到当前登录用户信息，然后判断用户的权限是否符合要求。

  这种方法最灵活，但是会写很多重复的代码，而且其他开发者无法一眼得知接口所需要的权限。ASMIm692hddgvLuS8K2U2nkkHTASdtzJju0lxnxR+D8=

  权限校验其实是一个比较通用的业务需求，一般会通过 **Spring AOP 切面 + 自定义权限校验注解 **实现统一的接口拦截和权限校验；如果有特殊的权限校验逻辑，再单独在接口中编码。

  💡 如果需要更复杂更灵活的权限控制，可以引入 Shiro / Spring Security / Sa-Token 等专门的权限管理框架。

**三、后端开发**

