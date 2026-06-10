create table if not exists duplicata (
    id varchar(64) primary key,
    user_id varchar(64) not null,
    montant integer not null,
    pdf_url varchar(500) not null,
    created_at timestamp not null default current_timestamp
);
