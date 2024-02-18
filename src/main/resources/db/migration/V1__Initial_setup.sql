create table users (
   id bigint auto_increment primary key,
   firstname varchar(50) not null,
   lastname varchar(50) not null,
   password varchar(64) not null,
   email varchar(100) not null,
   role varchar(40) not null,
   created_at timestamp not null default current_timestamp,
   updated_at timestamp
);

create table blogs (
   id bigint auto_increment primary key,
   user_id bigint not null,
   title varchar(200) not null,
   created_at timestamp not null default current_timestamp,
   updated_at timestamp,
   foreign key (user_id) references users(id)
);

create table posts (
   id bigint auto_increment primary key,
   blog_id bigint not null,
   title varchar(200) not null,
   content text not null,
   created_at timestamp not null default current_timestamp,
   updated_at timestamp,
   deleted_at timestamp,
   foreign key (blog_id) references blogs(id)
);