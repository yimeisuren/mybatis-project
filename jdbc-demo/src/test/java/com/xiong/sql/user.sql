use `jdbc`;
create table `user`
(
    id       int primary key,
    username varchar(20),
    password varchar(20)
);

insert into `user`
values (1, 'root', 'root');
insert into `user`
values (2, 'user', 'user');


# Statement的SQL注入问题
# SELECT `id`, `username`, `password`
# FROM `user`
# WHERE username = '1'
#    OR ' AND password = '
#    OR '1' = '1'