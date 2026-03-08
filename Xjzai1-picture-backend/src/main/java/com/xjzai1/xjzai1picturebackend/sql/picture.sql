-- auto-generated definition
create table picture
(
    id             bigint auto_increment comment 'id'
        primary key,
    url            varchar(512)                       not null comment '图片 url',
    name           varchar(128)                       not null comment '图片名称',
    introduction   varchar(512)                       null comment '简介',
    category       varchar(64)                        null comment '分类',
    tags           varchar(512)                       null comment '标签（JSON 数组）',
    picture_size   bigint                             null comment '图片体积',
    picture_width  int                                null comment '图片宽度',
    picture_height int                                null comment '图片高度',
    picture_scale  double                             null comment '图片宽高比例',
    picture_format varchar(32)                        null comment '图片格式',
    user_id        bigint                             not null comment '创建用户 id',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    edit_time      datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete      tinyint  default 0                 not null comment '是否删除',
    review_status  int      default 0                 not null comment '审核状态：0-待审核; 1-通过; 2-拒绝',
    review_message varchar(512)                       null comment '审核信息',
    reviewer_id    bigint                             null comment '审核人 ID',
    review_time    datetime                           null comment '审核时间',
    thumbnail_url  varchar(512)                       null comment '缩略图 url',
    original_url   varchar(512)                       null comment '原始图片 url',
    space_id       bigint   default 0                 null comment '空间 id（为空表示公共空间）',
    picture_color  varchar(16)                        null comment '图片主色调'
)
    comment '图片' COLLATE=utf8mb4_0900_ai_ci;

create index idx_category
    on picture (category);

create index idx_introduction
    on picture (introduction);

create index idx_name
    on picture (name);

create index idx_reviewStatus
    on picture (review_status);

create index idx_spaceId
    on picture (space_id);

create index idx_tags
    on picture (tags);

create index idx_userId
    on picture (user_id);

