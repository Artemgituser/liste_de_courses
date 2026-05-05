-- ─────────────────────────────────────────────────────────────────────────────
-- Base de données : users_shoppingg_sunny
-- À exécuter dans phpMyAdmin ou MySQL CLI
-- ─────────────────────────────────────────────────────────────────────────────

CREATE DATABASE IF NOT EXISTS users_shoppingg_sunny
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE users_shoppingg_sunny;

CREATE TABLE IF NOT EXISTS users (
    id         INT          NOT NULL AUTO_INCREMENT,
    login      VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,          -- stocké en bcrypt
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
