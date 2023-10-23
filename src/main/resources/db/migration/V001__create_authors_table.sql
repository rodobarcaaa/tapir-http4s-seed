create table "authors"
(
    "id"         UUID         NOT NULL PRIMARY KEY,
    "first_name" VARCHAR(255) NOT NULL,
    "last_name"  VARCHAR(255) NOT NULL
);
create unique index "idx_authors_unique_names" on "authors" ("first_name", "last_name");
