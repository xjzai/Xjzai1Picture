-- auto-generated definition
create table space_user
(
    id          bigint auto_increment comment 'id'
        primary key,
    space_id    bigint                                 not null comment '空间 id',
    user_id     bigint                                 not null comment '用户 id',
    space_role  varchar(128) default 'viewer'          null comment '空间角色：viewer/editor/admin',
    create_time datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_spaceId_userId
        unique (space_id, user_id)
)
    comment '空间用户关联' COLLATE=utf8mb4_0900_ai_ci;

create index idx_spaceId
    on space_user (space_id);

create index idx_userId
    on space_user (user_id);

