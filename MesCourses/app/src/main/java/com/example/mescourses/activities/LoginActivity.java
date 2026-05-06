package com.example.mescourses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.mescourses.R;
import com.example.mescourses.models.SessionManager;
import com.example.mescourses.network.ApiConfig;
import com.example.mescourses.network.MySingleton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout  tilLogin, tilPassword;
    private TextInputEditText etLogin, etPassword;
    private Button            btnLogin, btnGoRegister;
    private ProgressBar       progressBar;
    private SessionManager    session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(this);

        // Si déjà connecté → MainActivity
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        tilLogin      = findViewById(R.id.tilLogin);
        tilPassword   = findViewById(R.id.tilPassword);
        etLogin       = findViewById(R.id.etLogin);
        etPassword    = findViewById(R.id.etPassword);
        btnLogin      = findViewById(R.id.btnLogin);
        btnGoRegister = findViewById(R.id.btnGoRegister);
        progressBar   = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> validerEtConnnecter());

        btnGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void validerEtConnnecter() {
        String login = etLogin.getText() != null ? etLogin.getText().toString().trim() : "";
        String pwd   = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        boolean valid = true;

        // Contrôles de saisie
        if (login.isEmpty()) {
            tilLogin.setError("Le nom d'utilisateur est requis");
            valid = false;
        } else {
            tilLogin.setError(null);
        }

        if (pwd.isEmpty()) {
            tilPassword.setError("Le mot de passe est requis");
            valid = false;
        } else if (pwd.length() < 4) {
            tilPassword.setError("Mot de passe trop court (min. 4 caractères)");
            valid = false;
        } else {
            tilPassword.setError(null);
        }

        if (!valid) return;

        // Afficher chargement
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        StringRequest request = new StringRequest(Request.Method.POST,
            ApiConfig.URL_LOGIN,
            response -> {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean(ApiConfig.KEY_SUCCESS)) {
                        int    userId    = json.getInt(ApiConfig.KEY_USER_ID);
                        String userLogin = json.getString(ApiConfig.KEY_LOGIN);
                        String userEmail = json.getString(ApiConfig.KEY_EMAIL);

                        // Sauvegarde de la session
                        session.saveSession(userId, userLogin, userEmail);

                        Toast.makeText(this,
                            "Bienvenue " + userLogin + " !", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this,
                            json.getString(ApiConfig.KEY_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this,
                        "Erreur de réponse du serveur", Toast.LENGTH_LONG).show();
                }
            },
            error -> {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(this,
                    "Erreur réseau : vérifiez votre connexion", Toast.LENGTH_LONG).show();
            }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("login",    login);
                params.put("password", pwd);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
