create table member_profile
(
    id           bigint      not null auto_increment,
    birthday     date        not null,
    gender       varchar(10) not null,
    phone_number varchar(20) not null,
    created_at   datetime(6) not null,
    updated_at   datetime(6) not null,
    primary key (id)
) engine = InnoDB;

create table member_image
(
    id                 bigint       not null auto_increment,
    image_content_type varchar(10)  not null,
    original_file_name varchar(100) not null,
    server_file_path   varchar(255) not null,
    primary key (id)
) engine = InnoDB;

create table member
(
    id                bigint       not null auto_increment,
    identifier        varchar(50)  not null,
    password          varchar(255) not null,
    salt              varchar(255) not null,
    nickname          varchar(15)  not null,
    created_at        datetime(6)  not null,
    updated_at        datetime(6)  not null,
    member_profile_id bigint       not null,
    member_image_id   bigint       not null,
    primary key (id),
    constraint UK_member_member_profile_id unique (member_profile_id),
    constraint UK_member_identifier unique (identifier),
    constraint UK_member_member_image_id unique (member_image_id),
    constraint UK_member_nickname unique (nickname),
    constraint FK_member_member_profile_id
        foreign key (member_profile_id) references member_profile (id),
    constraint FK_member_member_image_id
        foreign key (member_image_id) references member_image (id)
) engine = InnoDB;

create table refresh_token
(
    id         bigint       not null auto_increment,
    token      varchar(255) not null,
    expired_at datetime(6)  not null,
    is_revoked bit          not null,
    member_id  bigint       not null,
    primary key (id),
    constraint FK_refresh_token_member_id
        foreign key (member_id) references member (id)
) engine = InnoDB;

create table roadmap_category
(
    id   bigint      not null auto_increment,
    name varchar(15) not null,
    primary key (id)
) engine = InnoDB;

create table roadmap
(
    id              bigint       not null auto_increment,
    title           varchar(50)  not null,
    introduction    varchar(200) not null,
    difficulty      varchar(30)  not null,
    required_period integer      not null,
    status          varchar(10)  not null,
    created_at      datetime(6)  not null,
    category_id     bigint       not null,
    member_id       bigint       not null,
    primary key (id),
    constraint FK_roadmap_roadmap_category_id
        foreign key (category_id) references roadmap_category (id),
    constraint FK_roadmap_member_id
        foreign key (member_id) references member (id)
) engine = InnoDB;

create table roadmap_content
(
    id         bigint      not null auto_increment,
    content    varchar(2200),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    roadmap_id bigint      not null,
    primary key (id),
    constraint FK_roadmap_content_roadmap_id
        foreign key (roadmap_id) references roadmap (id)
) engine = InnoDB;

create table roadmap_node
(
    id                 bigint        not null auto_increment,
    title              varchar(50)   not null,
    content            varchar(2200) not null,
    roadmap_content_id bigint        not null,
    primary key (id),
    constraint FK_roadmap_node_roadmap_content_id
        foreign key (roadmap_content_id) references roadmap_content (id)
) engine = InnoDB;

create table roadmap_node_image
(
    id                 bigint       not null auto_increment,
    image_content_type varchar(10)  not null,
    original_file_name varchar(100) not null,
    server_file_path   varchar(255) not null,
    roadmap_node_id    bigint       not null,
    primary key (id),
    constraint FK_roadmap_node_image_roadmap_node_id
        foreign key (roadmap_node_id) references roadmap_node (id)
) engine = InnoDB;

create table goal_room
(
    id                   bigint      not null auto_increment,
    name                 varchar(50) not null,
    limited_member_count integer     not null,
    status               varchar(30) not null,
    start_date           date        not null,
    end_date             date        not null,
    created_at           datetime(6) not null,
    updated_at           datetime(6) not null,
    roadmap_content_id   bigint      not null,
    primary key (id),
    constraint FK_goal_room_roadmap_content_id
        foreign key (roadmap_content_id) references roadmap_content (id)
) engine = InnoDB;

create table goal_room_pending_member
(
    id           bigint      not null auto_increment,
    role         varchar(15) not null,
    joined_at    datetime(6),
    goal_room_id bigint      not null,
    member_id    bigint      not null,
    primary key (id),
    constraint FK_goal_room_pending_member_goal_room_id
        foreign key (goal_room_id) references goal_room (id),
    constraint FK_goal_room_pending_member_member_id
        foreign key (member_id) references member (id)
) engine = InnoDB;

create table goal_room_member
(
    id                  bigint      not null auto_increment,
    accomplishment_rate float(53),
    role                varchar(15) not null,
    joined_at           datetime(6),
    goal_room_id        bigint      not null,
    member_id           bigint      not null,
    primary key (id),
    constraint FK_goal_room_member_goal_room_id
        foreign key (goal_room_id) references goal_room (id),
    constraint FK_goal_room_member_member_id
        foreign key (member_id) references member (id)
) engine = InnoDB;

create table goal_room_to_do
(
    id           bigint       not null auto_increment,
    content      varchar(300) not null,
    start_date   date         not null,
    end_date     date         not null,
    created_at   datetime(6)  not null,
    updated_at   datetime(6)  not null,
    goal_room_id bigint       not null,
    primary key (id),
    constraint FK_goal_room_to_do_goal_room_id
        foreign key (goal_room_id) references goal_room (id)
) engine = InnoDB;

create table goal_room_roadmap_node
(
    id              bigint  not null auto_increment,
    start_date      date    not null,
    end_date        date    not null,
    check_count     integer not null,
    goal_room_id    bigint  not null,
    roadmap_node_id bigint  not null,
    primary key (id),
    constraint FK_goal_room_roadmap_node_roadmap_node_id
        foreign key (roadmap_node_id) references roadmap_node (id),
    constraint goal_room_roadmap_node_goal_room_id
        foreign key (goal_room_id) references goal_room (id)
) engine = InnoDB;

create table check_feed
(
    id                        bigint       not null auto_increment,
    server_file_path          varchar(255) not null,
    image_content_type        varchar(255) not null,
    original_file_name        varchar(255) not null,
    description               varchar(255),
    created_at                timestamp(6) not null,
    goal_room_roadmap_node_id bigint       not null,
    goal_room_member_id       bigint       not null,
    primary key (id),
    constraint FK_check_feed_goal_room_roadmap_node_id
        foreign key (goal_room_roadmap_node_id) references goal_room_roadmap_node (id),
    constraint FK_check_feed_goal_room_member_id
        foreign key (goal_room_member_id) references goal_room_member (id)
) engine = InnoDB;

create table roadmap_review
(
    id         bigint      not null auto_increment,
    rate       float(53)   not null,
    content    varchar(1200),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    member_id  bigint      not null,
    roadmap_id bigint      not null,
    primary key (id),
    constraint FK_roadmap_review_roadmap_id
        foreign key (roadmap_id) references roadmap (id),
    constraint FK_roadmap_review_member_id
        foreign key (member_id) references member (id)
) engine = InnoDB;

create table roadmap_tag
(
    id         bigint      not null auto_increment,
    name       varchar(15) not null,
    roadmap_id bigint      not null,
    primary key (id),
    constraint FK_roadmap_tag_roadmap_id
        foreign key (roadmap_id) references roadmap (id)
) engine = InnoDB;

create table goal_room_to_do_check
(
    id                  bigint not null auto_increment,
    goal_room_member_id bigint not null,
    goal_room_to_do_id  bigint not null,
    primary key (id),
    constraint FK_goal_room_to_do_check_goal_room_member_id
        foreign key (goal_room_member_id) references goal_room_member (id),
    constraint FK_goal_room_to_do_check_goal_room_to_do_id
        foreign key (goal_room_to_do_id) references goal_room_to_do (id)
) engine = InnoDB;
