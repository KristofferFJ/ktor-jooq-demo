create table estate
(
    id   bigserial primary key,
    name text not null,
    some_date date,
    company_id int8 not null references company(id)
)