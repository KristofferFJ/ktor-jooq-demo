create table booking_set
(
    id         bigserial primary key,
    created_at timestamp not null,
    estate_id  int8 references estate (id),
    company_id int8 references company (id),

    CONSTRAINT chk_company_or_estate CHECK (
            (company_id IS NOT NULL AND estate_id IS NULL) OR
            (company_id IS NULL AND estate_id IS NOT NULL)
        )
);

create table booking
(
    id             bigserial primary key,
    amount         DECIMAL(10, 2) not null,
    booking_set_id int8 references booking_set,
    account_id     int8 references account (id)
)