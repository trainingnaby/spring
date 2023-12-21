
create table duplicata (
	id varchar(255) not null,
	user_id varchar(255) not null,
	montant integer not null,
	pdfurl varchar(255) not null,
	primary key(id)
);

create table users
	(
	   id varchar(255) not null,
	   username  varchar(255) not null,
	   password varchar(255) not null,
	   enabled integer not null,
	   authorities varchar(255) not null,
	   primary key(id)
	);