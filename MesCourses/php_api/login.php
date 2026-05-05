<?php
// ─── login.php ────────────────────────────────────────────────────────────────
// Méthode : POST
// Paramètres : login, password
// Réponse    : JSON { success, message, user_id, login, email }
// ─────────────────────────────────────────────────────────────────────────────

header('Access-Control-Allow-Origin: *');
header('Content-Type: application/json; charset=utf-8');

require_once 'db_connect.php';

// Vérification méthode HTTP
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(false, 'Méthode non autorisée');
}

// Récupération + nettoyage des champs
$login    = isset($_POST['login'])    ? trim($_POST['login'])    : '';
$password = isset($_POST['password']) ? trim($_POST['password']) : '';

// Validations basiques
if (empty($login) || empty($password)) {
    jsonResponse(false, 'Tous les champs sont requis');
}

try {
    $pdo  = getConnection();

    // Recherche de l'utilisateur par login OU email
    $stmt = $pdo->prepare(
        'SELECT id, login, email, password
           FROM users
          WHERE login = :login OR email = :login
          LIMIT 1'
    );
    $stmt->execute([':login' => $login]);
    $user = $stmt->fetch();

    if (!$user) {
        // Utilisateur introuvable — message générique pour la sécurité
        jsonResponse(false, 'Identifiants incorrects');
    }

    // Vérification bcrypt
    if (!password_verify($password, $user['password'])) {
        jsonResponse(false, 'Identifiants incorrects');
    }

    // Connexion réussie
    jsonResponse(true, 'Connexion réussie', [
        'user_id' => (int) $user['id'],
        'login'   => $user['login'],
        'email'   => $user['email'],
    ]);

} catch (PDOException $e) {
    jsonResponse(false, 'Erreur serveur : ' . $e->getMessage());
}
