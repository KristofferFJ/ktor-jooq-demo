create table account
(
    id   bigserial primary key,
    name text not null,
    number int4 not null,
    company_id int8 not null references company(id)
)