--liquibase formatted sql

--changeset andrii.kolomoiets:populate-roles-table
-- NOSONAR: Test data for development environment only
INSERT INTO public.users (email, username, password)
VALUES ('admin@test.com', 'admin', '$2a$12$xOarQw5QYcQPQ2aDwt735O16bFrImVU7TsTtLvRQ/.G4GgP9jksP.'),
       ('user@test.com', 'user', '$2a$12$3ImN.bURmPo2l32xbGXNReBwoYeV0vfnm7LQl3ch3D9HqGdNMb6ve');
