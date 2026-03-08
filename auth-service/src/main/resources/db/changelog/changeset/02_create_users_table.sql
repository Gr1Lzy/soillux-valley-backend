--liquibase formatted sql

--changeset andrii.kolomoiets:create-users-table
CREATE TABLE IF NOT EXISTS public.users
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email      VARCHAR(50)  NOT NULL UNIQUE,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP()
);
