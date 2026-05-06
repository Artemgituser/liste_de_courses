<?php
// ─── register.php ─────────────────────────────────────────────────────────────
// Méthode : POST
// Paramètres : login, email, password
// Réponse    : JSON { success, message, user_id, login, email }
// ─────────────────────────────────────────────────────────────────────────────

header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json; charset=utf-8');

require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(false, 'Méthode non autorisée');
}

$login    = isset($_POST['login'])    ? trim($_POST['login'])    : '';
$email    = isset($_POST['email'])    ? trim($_POST['email'])    : '';
$password = isset($_POST['password']) ? trim($_POST['password']) : '';

// ─── Validations ──────────────────────────────────────────────────────────────
if (empty($login) || empty($email) || empty($password)) {
    jsonResponse(false, 'Tous les champs sont requis');
}

if (strlen($login) < 3) {
    jsonResponse(false, 'Le nom d\'utilisateur doit contenir au moins 3 caractères');
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(false, 'Adresse e-mail invalide');
}

if (strlen($password) < 6) {
    jsonResponse(false, 'Le mot de passe doit contenir au moins 6 caractères');
}

try {
    $pdo = getConnection();

    // Vérification doublon login
    $stmtCheck = $pdo->prepare(
        'SELECT id FROM users WHERE login = :login OR email = :email LIMIT 1'
    );
    $stmtCheck->execute([':login' => $login, ':email' => $email]);

    if ($stmtCheck->fetch()) {
        jsonResponse(false, 'Ce nom d\'utilisateur ou cet e-mail est déjà utilisé');
    }

    // Hash du mot de passe (bcrypt)
    $hashedPwd = password_hash($password, PASSWORD_BCRYPT);

    // Insertion
    $stmt = $pdo->prepare(
        'INSERT INTO users (login, email, password) VALUES (:login, :email, :password)'
    );
    $stmt->execute([
        ':login'    => $login,
        ':email'    => $email,
        ':password' => $hashedPwd,
    ]);

    $newId = (int) $pdo->lastInsertId();

    jsonResponse(true, 'Compte créé avec succès', [
        'user_id' => $newId,
        'login'   => $login,
        'email'   => $email,
    ]);

} catch (PDOException $e) {
    jsonResponse(false, 'Erreur serveur : ' . $e->getMessage());
}
