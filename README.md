# liste_de_courses
## 📱 Présentation

Mes Courses est une application mobile Android permettant d'organiser une liste de courses en temps réel.

###  Fonctionnalités principales

| Fonctionnalité | Description |
|---|---|
|  Authentification | Connexion / Inscription sécurisée via Volley + MySQL/PHP |
|  Gestion produits | Ajout, modification, suppression avec vérification de doublon |
|  Liste de courses | Courses en cours triées par rayon, swipe pour gérer |
|  Archivage | Base de tous les produits, swipe pour ajouter à la liste |
|  Profil | Modification email/mot de passe, mode sombre |
|  Dark Mode | Switch instantané depuis le profil |

---

## 🏗️ Architecture
```
MesCourses/
├── app/src/main/
│   ├── java/com/example/mescourses/
│   │   ├── activities/          # 9 activités Android
│   │   │   ├── SplashActivity       # Splash + animation
│   │   │   ├── LoginActivity        # Connexion Volley
│   │   │   ├── RegisterActivity     # Inscription Volley
│   │   │   ├── MainActivity         # Dashboard
│   │   │   ├── ProfilActivity       # Compte utilisateur
│   │   │   ├── ProduitsActivity     # CRUD produits SQLite
│   │   │   ├── CoursesActivity      # Liste courses + swipe
│   │   │   ├── ArchivageActivity    # Archivage + swipe + FAB
│   │   │   └── ModifQuantiteActivity # Modification produit
│   │   ├── adapters/            # RecyclerView adapters
│   │   │   ├── ProduitAdapter
│   │   │   ├── CourseAdapter
│   │   │   └── ArchivageAdapter
│   │   ├── database/
│   │   │   └── DatabaseHelper   # SQLite CRUD
│   │   ├── models/
│   │   │   ├── Produit          # Modèle produit
│   │   │   └── SessionManager   # SharedPreferences session
│   │   └── network/
│   │       ├── VolleySingleton  # File de requêtes
│   │       └── ApiConfig        # URLs + clés JSON
│   └── res/
│       ├── layout/              # 8 layouts XML
│       ├── drawable/            # Backgrounds, badges, swipe
│       └── values/              # Colors, strings, themes, dimens
└── php_api/                     # Backend PHP/MySQL
    ├── db_connect.php
    ├── login.php
    ├── register.php
    ├── change_password.php
    ├── change_email.php
    └── create_table.sql
```
---
