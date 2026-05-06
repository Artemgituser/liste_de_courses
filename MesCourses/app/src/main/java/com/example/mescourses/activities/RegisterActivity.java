package com.example.mescourses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout   tilLogin, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText etLogin, etEmail, etPassword, etConfirm;
    private Button            btnRegister, btnGoLogin;
    private ProgressBar       progressBar;
    private SessionManager    session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session       = new SessionManager(this);
        tilLogin      = findViewById(R.id.tilLogin);
        tilEmail      = findViewById(R.id.tilEmail);
        tilPassword   = findViewById(R.id.tilPassword);
        tilConfirm    = findViewById(R.id.tilConfirmPassword);
        etLogin       = findViewById(R.id.etLogin);
        etEmail       = findViewById(R.id.etEmail);
        etPassword    = findViewById(R.id.etPassword);
        etConfirm     = findViewById(R.id.etConfirmPassword);
        btnRegister   = findViewById(R.id.btnRegister);
        btnGoLogin    = findViewById(R.id.btnGoLogin);
        progressBar   = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> validerEtInscrire());
        btnGoLogin.setOnClickListener(v -> finish());
    }

    private void validerEtInscrire() {
        String login   = etLogin.getText()   != null ? etLogin.getText().toString().trim()   : "";
        String email   = etEmail.getText()   != null ? etEmail.getText().toString().trim()   : "";
        String pwd     = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirm = etConfirm.getText() != null ? etConfirm.getText().toString().trim() : "";

        boolean valid = true;

        if (login.isEmpty() || login.length() < 3) {
            tilLogin.setError("Min. 3 caractères");
            valid = false;
        } else {
            tilLogin.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Adresse e-mail invalide");
            valid = false;
        } else {
            tilEmail.setError(null);
        }

        if (pwd.isEmpty() || pwd.length() < 6) {
            tilPassword.setError("Min. 6 caractères");
            valid = false;
        } else {
            tilPassword.setError(null);
        }

        if (!confirm.equals(pwd)) {
            tilConfirm.setError("Les mots de passe ne correspondent pas");
            valid = false;
        } else {
            tilConfirm.setError(null);
        }

        if (!valid) return;

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        StringRequest request = new StringRequest(Request.Method.POST,
            ApiConfig.URL_REGISTER,
            response -> {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean(ApiConfig.KEY_SUCCESS)) {
                        int    userId    = json.getInt(ApiConfig.KEY_USER_ID);
                        String userLogin = json.getString(ApiConfig.KEY_LOGIN);
                        String userEmail = json.getString(ApiConfig.KEY_EMAIL);

                        session.saveSession(userId, userLogin, userEmail);

                        Toast.makeText(this,
                            "Compte créé ! Bienvenue " + userLogin, Toast.LENGTH_SHORT).show();

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
                btnRegister.setEnabled(true);
                Toast.makeText(this,
                    "Erreur réseau : vérifiez votre connexion", Toast.LENGTH_LONG).show();
            }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("login",    login);
                params.put("email",    email);
                params.put("password", pwd);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
