package com.example.mescourses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.mescourses.R;
import com.example.mescourses.models.SessionManager;
import com.example.mescourses.database.DatabaseHelper;
import com.example.mescourses.network.ApiConfig;
import com.example.mescourses.network.MySingleton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfilActivity extends AppCompatActivity {

    private SessionManager session;
    private TextView       tvUsername, tvEmail, tvUserInitials;
    private TextView       tvStatTotal, tvStatLastDate;
    private LinearLayout   rowChangePassword, rowChangeEmail, rowLogout;
    private SwitchMaterial switchDarkMode;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        session            = new SessionManager(this);
        db                 = DatabaseHelper.getInstance(this);
        tvUsername         = findViewById(R.id.tvUsername);
        tvEmail            = findViewById(R.id.tvEmail);
        tvUserInitials     = findViewById(R.id.tvUserInitials);
        tvStatTotal        = findViewById(R.id.tvStatTotalProducts);
        tvStatLastDate     = findViewById(R.id.tvStatLastDate);
        rowChangePassword  = findViewById(R.id.rowChangePassword);
        rowChangeEmail     = findViewById(R.id.rowChangeEmail);
        rowLogout          = findViewById(R.id.rowLogout);
        switchDarkMode     = findViewById(R.id.switchDarkMode);

        // Afficher les infos utilisateur
        String login = session.getLogin();
        String email = session.getEmail();
        tvUsername.setText(login);
        tvEmail.setText(email);
        if (!login.isEmpty()) {
            tvUserInitials.setText(String.valueOf(login.charAt(0)).toUpperCase());
        }

        // Dark mode switch
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        switchDarkMode.setChecked(nightMode == AppCompatDelegate.MODE_NIGHT_YES);
        switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            AppCompatDelegate.setDefaultNightMode(
                isChecked ? AppCompatDelegate.MODE_NIGHT_YES
                          : AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Changer mot de passe
        rowChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // Changer email
        rowChangeEmail.setOnClickListener(v -> showChangeEmailDialog());

        // Stats
        chargerStats();

        // Déconnexion
        rowLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setPositiveButton("Oui", (d, w) -> {
                    session.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Annuler", null)
                .show();
        });
    }

    /** Dialog changement de mot de passe */
    private void showChangePasswordDialog() {
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_register, null);

        // Repurposing activity_register layout for consistency
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText("Nouveau mot de passe");

        EditText etOld = new EditText(this);
        etOld.setHint("Ancien mot de passe");
        etOld.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // The activity_register already has fields for login, email, password.
        // Let's use them: etLogin (repurposed for old), etEmail (repurposed for new), etPassword (confirm)
        EditText etO = v.findViewById(R.id.etLogin);
        EditText etN = v.findViewById(R.id.etEmail);
        EditText etC = v.findViewById(R.id.etPassword);
        android.widget.Button btn = v.findViewById(R.id.btnRegister);
        btn.setVisibility(View.GONE);

        etO.setHint("Ancien mot de passe");
        etO.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        etN.setHint("Nouveau mot de passe");
        etN.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        etC.setHint("Confirmer nouveau mot de passe");

        new AlertDialog.Builder(this)
            .setView(v)
            .setPositiveButton("Modifier", (d, w) -> {
                String oldPwd  = etO.getText().toString().trim();
                String newPwd  = etN.getText().toString().trim();
                String confirm = etC.getText().toString().trim();

                if (oldPwd.isEmpty() || newPwd.isEmpty()) {
                    Toast.makeText(this, "Remplissez tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPwd.length() < 6) {
                    Toast.makeText(this, "Nouveau mot de passe trop court", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPwd.equals(confirm)) {
                    Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                    return;
                }
                changerMotDePasse(oldPwd, newPwd);
            })
            .setNegativeButton("Annuler", null)
            .show();
    }

    /** Dialog changement d'email */
    private void showChangeEmailDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);

        EditText etNewEmail = new EditText(this);
        etNewEmail.setHint("Nouvelle adresse e-mail");
        etNewEmail.setInputType(android.text.InputType.TYPE_CLASS_TEXT
            | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etNewEmail.setText(session.getEmail());
        layout.addView(etNewEmail);

        new AlertDialog.Builder(this)
            .setTitle("Changer l'e-mail")
            .setView(layout)
            .setPositiveButton("Modifier", (d, w) -> {
                String newEmail = etNewEmail.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    Toast.makeText(this, "E-mail invalide", Toast.LENGTH_SHORT).show();
                    return;
                }
                changerEmail(newEmail);
            })
            .setNegativeButton("Annuler", null)
            .show();
    }

    /** Volley POST — changement mot de passe */
    private void changerMotDePasse(String oldPwd, String newPwd) {
        StringRequest request = new StringRequest(Request.Method.POST,
            ApiConfig.URL_CHANGE_PASSWORD,
            response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    Toast.makeText(this,
                        json.getString(ApiConfig.KEY_MESSAGE), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(this, "Erreur serveur", Toast.LENGTH_SHORT).show();
                }
            },
            error -> Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("user_id",      String.valueOf(session.getUserId()));
                p.put("old_password", oldPwd);
                p.put("new_password", newPwd);
                return p;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void chargerStats() {
        tvStatTotal.setText(String.valueOf(db.getTotalProductsPurchased()));
        tvStatLastDate.setText(db.getLastShoppingDate());
    }

    /** Volley POST — changement email */
    private void changerEmail(String newEmail) {
        StringRequest request = new StringRequest(Request.Method.POST,
            ApiConfig.URL_CHANGE_EMAIL,
            response -> {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getBoolean(ApiConfig.KEY_SUCCESS)) {
                        // Mettre à jour la session
                        session.updateEmail(newEmail);
                        tvEmail.setText(newEmail);
                    }
                    Toast.makeText(this,
                        json.getString(ApiConfig.KEY_MESSAGE), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(this, "Erreur serveur", Toast.LENGTH_SHORT).show();
                }
            },
            error -> Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("user_id",   String.valueOf(session.getUserId()));
                p.put("new_email", newEmail);
                return p;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}
