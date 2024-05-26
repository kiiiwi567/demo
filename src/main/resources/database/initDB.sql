create type "userRole" as enum('Employee','Manager','Engineer');
create type "ticketState" as enum ('Draft','New','Approved','Declined','In_progress','Done','Cancelled');
create type "ticketUrgency" as enum ('Critical','High','Average','Low');

create table if not exists category
(
    id serial primary key,
    "name" varchar(30)
);

create table if not exists "user"
(
    id serial primary key,
    first_name varchar(15) not null,
    last_name varchar (15),
    "role" "userRole" not null,
    email varchar(100) not null,
    "password" varchar(20) not null
);

create table if not exists ticket
(
    id serial primary key,
    "name" varchar(100) not null,
    description varchar (500),
    created_on date not null,
    desired_resolution_date date,
    assignee_id int references "user" (id),
    owner_id int references "user" (id),
    "state" "ticketState" not null,
    category_id int references category (id),
    urgency "ticketUrgency",
    approver_id int references "user" (id)
);

create table if not exists attachment
(
    id serial primary key,
    contents bytea,
    ticket_id int references ticket (id) ON DELETE CASCADE,
    "name" varchar(50)
);

create table if not exists history
(
    id serial primary key,
    ticket_id int references ticket(id) ON DELETE CASCADE,
    "timestamp" timestamp without time zone,
    "action" varchar(50),
    user_id int references "user" (id),
    description varchar(100)
);

create table if not exists "comment"
(
    id serial primary key,
    user_id int references "user" (id),
    "text" varchar(500),
    "timestamp" timestamp without time zone,
    ticket_id int references ticket (id) ON DELETE CASCADE
);

create table if not exists feedback
(
    id serial primary key,
    user_id int references "user" (id),
    rate int,
    "date" date,
    "text" varchar(500),
    ticket_id int references ticket (id) ON DELETE CASCADE
);