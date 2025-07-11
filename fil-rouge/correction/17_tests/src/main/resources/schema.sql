create table if not exists duplicatas
	(
		id      uuid  default random_uuid() primary key,
		pdf_url varchar(255),
		user_id varchar(255),
		montant  int
	);