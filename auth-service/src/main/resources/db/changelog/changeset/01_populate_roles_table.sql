--liquibase formatted sql

--changeset andrii.kolomoiets:populate-roles-table
INSERT INTO public.roles (name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_USER');
