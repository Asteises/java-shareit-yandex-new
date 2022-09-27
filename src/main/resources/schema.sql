create table if not exists USERS
(
    ID bigint generated by default as identity primary key,
    NAME varchar(255) not null,
    EMAIL varchar(512) not null unique
);

create table if not exists REQUESTS
(
    ID bigint generated by default as identity primary key,
    DESCRIPTION varchar(1024),
    REQUESTOR bigint unique not null,
    CREATED timestamp,
    foreign key (REQUESTOR) references USERS (ID) on delete  cascade
);

create table if not exists ITEMS
(
    ID bigint generated by default as identity primary key,
    NAME varchar(255) not null,
    DESCRIPTION varchar(1024),
    AVAILABLE boolean,
    OWNER_ID bigint,
    REQUEST_ID bigint,
    foreign key (OWNER_ID) references USERS (ID) on delete cascade,
    foreign key (REQUEST_ID) references REQUESTS (ID)
);

create table if not exists BOOKINGS
(
    ID bigint generated by default as identity primary key,
    START_DATE timestamp,
    END_DATE timestamp,
    ITEM_ID bigint not null,
    BOOKER_ID bigint not null,
    STATUS varchar,
    foreign key (ITEM_ID) references ITEMS (ID) on delete cascade,
    foreign key (BOOKER_ID) references USERS (ID) on delete cascade
);

create table if not exists COMMENTS
(
    ID bigint generated by default as identity primary key,
    TEXT varchar(1024),
    ITEM_ID bigint not null,
    AUTHOR_ID bigint not null,
    CREATED timestamp,
    foreign key (ITEM_ID) references ITEMS (ID) on delete cascade,
    foreign key (AUTHOR_ID) references USERS (ID) on delete cascade
);