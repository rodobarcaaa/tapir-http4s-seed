create table "users"
(
    "id"            UUID         NOT NULL PRIMARY KEY,
    "username"      VARCHAR(255) NOT NULL UNIQUE,
    "email"         VARCHAR(255) NOT NULL UNIQUE,
    "password_hash" TEXT         NOT NULL,
    "created_at"    TIMESTAMP    NOT NULL,
    "updated_at"    TIMESTAMP    NOT NULL
);
create unique index "idx_users_unique_username" on "users" ("username");
create unique index "idx_users_unique_email" on "users" ("email");