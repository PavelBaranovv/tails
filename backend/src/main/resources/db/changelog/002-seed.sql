--liquibase formatted sql

--changeset tails:002-seed
INSERT INTO district (id, name) VALUES
    (1, 'Адмиралтейский'),
    (2, 'Василеостровский'),
    (3, 'Выборгский'),
    (4, 'Калининский'),
    (5, 'Кировский'),
    (6, 'Колпинский'),
    (7, 'Красногвардейский'),
    (8, 'Красносельский'),
    (9, 'Кронштадтский'),
    (10, 'Курортный'),
    (11, 'Московский'),
    (12, 'Невский'),
    (13, 'Петроградский'),
    (14, 'Петродворцовый'),
    (15, 'Приморский'),
    (16, 'Пушкинский'),
    (17, 'Фрунзенский'),
    (18, 'Центральный');

ALTER TABLE district ALTER COLUMN id RESTART WITH 19;

INSERT INTO admin (email, password_hash, full_name) VALUES
    ('admin@tails.local', '$2b$10$BLukEsfTWEpyJ/wb5l/I8ujzyCp.4z1wYTOi3VflGdjcRgeSM4BX6', 'Администратор');

INSERT INTO shelter (name, district_id, address, phone, email, password_hash, status) VALUES
    ('Добрые лапы', 18, 'ул. Примерная, 10', '+79991234567', 'shelter@tails.local', '$2b$10$hOXp8deJxyC80PkgLGfn5uVyXqrIANkAk2LELmfBvZmFiW2nStjka', 'ACTIVE');
