create table "publishers"
(
    "id"   UUID         NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL UNIQUE,
    "url"  VARCHAR(255) NOT NULL UNIQUE
);
