<?php
// ─── change_email.php ─────────────────────────────────────────────────────────
// Méthode : POST
// Paramètres : user_id, new_email
// Réponse    : JSON { success, message }
// ─────────────────────────────────────────────────────────────────────────────

header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json; charset=utf-8');

require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(false, 'Méthode non autorisée');
}

$userId   = isset($_POST['user_id'])   ? (int) $_POST['user_id']      : 0;
$newEmail = isset($_POST['new_email']) ? trim($_POST['new_email'])     : '';

if ($userId <= 0 || empty($newEmail)) {
    jsonResponse(false, 'Tous les champs sont requis');
}

if (!filter_var($newEmail, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(false, 'Adresse e-mail invalide');
}

try {
    $pdo = getConnection();

    // Vérifier que l'email n'est pas déjà utilisé par un autre compte
    $check = $pdo->prepare(
        'SELECT id FROM users WHERE email = :email AND id != :id LIMIT 1'
    );
    $check->execute([':email' => $newEmail, ':id' => $userId]);

    if ($check->fetch()) {
        jsonResponse(false, 'Cet e-mail est déjà utilisé par un autre compte');
    }

    // Mettre à jour
    $update = $pdo->prepare('UPDATE users SET email = :email WHERE id = :id');
    $update->execute([':email' => $newEmail, ':id' => $userId]);

    jsonResponse(true, 'Adresse e-mail mise à jour avec succès');

} catch (PDOException $e) {
    jsonResponse(false, 'Erreur serveur : ' . $e->getMessage());
}
