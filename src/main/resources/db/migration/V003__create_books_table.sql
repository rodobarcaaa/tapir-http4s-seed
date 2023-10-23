create table "books" ("id" UUID NOT NULL PRIMARY KEY,"isbn" VARCHAR(255) NOT NULL UNIQUE,"title" VARCHAR(255) NOT NULL UNIQUE,"description" VARCHAR NOT NULL,"year" INTEGER NOT NULL,"publisher_id" UUID NOT NULL,"author_id" UUID NOT NULL);
alter table "books" add constraint "book_author_id_fk" foreign key("author_id") references "authors"("id") on update RESTRICT on delete RESTRICT;
alter table "books" add constraint "book_publisher_id_fk" foreign key("publisher_id") references "publishers"("id") on update RESTRICT on delete RESTRICT;
