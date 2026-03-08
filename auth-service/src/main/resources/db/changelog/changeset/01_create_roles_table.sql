--liquibase formatted sql

--changeset andrii.kolomoiets:create-roles-table
CREATE TABLE IF NOT EXISTS public.roles
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP()
);
