use `jdbc`;
drop table if exists `person`;
create table `person`
(
    id       int primary key,
    name     varchar(20),
    age      int,
    birthday date
);

insert `person`
values (1, 'root', 18, '2000-11-11');

insert `person`
values (2, 'user', 19, '2000-11-11');

insert `person`
values (3, 'admin', 19, '2000-11-11');