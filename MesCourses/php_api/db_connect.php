<?php
// ─── Configuration base de données ───────────────────────────────────────────
define('DB_HOST',     'localhost');
define('DB_NAME',     'users_shoppingg_sunny');
define('DB_USER',     'root');
define('DB_PASSWORD', '');          // mot de passe vide selon l'énoncé
define('DB_CHARSET',  'utf8mb4');

// ─── Connexion PDO ────────────────────────────────────────────────────────────
function getConnection(): PDO {
    $dsn = "mysql:host=" . DB_HOST
         . ";dbname=" . DB_NAME
         . ";charset=" . DB_CHARSET;

    $options = [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES   => false,
    ];

    return new PDO($dsn, DB_USER, DB_PASSWORD, $options);
}

// ─── Helper : réponse JSON ────────────────────────────────────────────────────
function jsonResponse(bool $success, string $message, array $data = []): void {
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode(array_merge(
        ['success' => $success, 'message' => $message],
        $data
    ));
    exit;
}
