-- auto-generated definition
create table space
(
    id          bigint auto_increment comment 'id'
        primary key,
    space_name  varchar(128)                       null comment '空间名称',
    space_level int      default 0                 null comment '空间级别：0-普通版 1-专业版 2-旗舰版',
    max_size    bigint   default 0                 null comment '空间图片的最大总大小',
    max_count   bigint   default 0                 null comment '空间图片的最大数量',
    total_size  bigint   default 0                 null comment '当前空间下图片的总大小',
    total_count bigint   default 0                 null comment '当前空间下的图片数量',
    user_id     bigint                             not null comment '创建用户 id',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    edit_time   datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    space_type  int      default 0                 not null comment '空间类型：0-私有 1-团队'
)
    comment '空间' COLLATE=utf8mb4_0900_ai_ci;

create index idx_spaceLevel
    on space (space_level);

create index idx_spaceName
    on space (space_name);

create index idx_spaceType
    on space (space_type);

create index idx_userId
    on space (user_id);

