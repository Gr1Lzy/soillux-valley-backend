--liquibase formatted sql

--changeset andrii.kolomoiets:populate-user-roles-table
INSERT INTO public.user_roles (user_id, role_id)
VALUES (1, 1),
       (1, 2),
       (2, 2);
