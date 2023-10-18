create table estate
(
    id   bigserial primary key,
    name text not null,
    company_id int8 not null references company(id)
)