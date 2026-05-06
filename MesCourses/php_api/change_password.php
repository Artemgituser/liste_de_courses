<?php
// ─── change_password.php ──────────────────────────────────────────────────────
// Méthode : POST
// Paramètres : user_id, old_password, new_password
// Réponse    : JSON { success, message }
// ─────────────────────────────────────────────────────────────────────────────

header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json; charset=utf-8');

require_once 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(false, 'Méthode non autorisée');
}

$userId      = isset($_POST['user_id'])      ? (int) $_POST['user_id']             : 0;
$oldPassword = isset($_POST['old_password']) ? trim($_POST['old_password'])        : '';
$newPassword = isset($_POST['new_password']) ? trim($_POST['new_password'])        : '';

if ($userId <= 0 || empty($oldPassword) || empty($newPassword)) {
    jsonResponse(false, 'Tous les champs sont requis');
}

if (strlen($newPassword) < 6) {
    jsonResponse(false, 'Le nouveau mot de passe doit contenir au moins 6 caractères');
}

try {
    $pdo = getConnection();

    // Récupérer le hash actuel
    $stmt = $pdo->prepare('SELECT password FROM users WHERE id = :id LIMIT 1');
    $stmt->execute([':id' => $userId]);
    $user = $stmt->fetch();

    if (!$user) {
        jsonResponse(false, 'Utilisateur introuvable');
    }

    // Vérifier l'ancien mot de passe
    if (!password_verify($oldPassword, $user['password'])) {
        jsonResponse(false, 'Ancien mot de passe incorrect');
    }

    // Mettre à jour avec le nouveau hash
    $newHash = password_hash($newPassword, PASSWORD_BCRYPT);
    $update  = $pdo->prepare('UPDATE users SET password = :pwd WHERE id = :id');
    $update->execute([':pwd' => $newHash, ':id' => $userId]);

    jsonResponse(true, 'Mot de passe modifié avec succès');

} catch (PDOException $e) {
    jsonResponse(false, 'Erreur serveur : ' . $e->getMessage());
}
