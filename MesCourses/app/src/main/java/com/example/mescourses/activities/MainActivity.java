package com.example.mescourses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mescourses.R;
import com.example.mescourses.models.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);

        // Vérification session
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Salutation personnalisée
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        tvGreeting.setText("Bonjour, " + session.getLogin() + " !");

        // Navigation cards
        CardView cardProduits  = findViewById(R.id.cardProduits);
        CardView cardCourses   = findViewById(R.id.cardCourses);
        CardView cardArchivage = findViewById(R.id.cardArchivage);
        CardView cardProfil    = findViewById(R.id.cardProfil);

        cardProduits.setOnClickListener(v ->
            startActivity(new Intent(this, ProduitsActivity.class)));

        cardCourses.setOnClickListener(v ->
            startActivity(new Intent(this, ListManagementActivity.class)));

        cardArchivage.setOnClickListener(v ->
            startActivity(new Intent(this, ArchivageActivity.class)));

        cardProfil.setOnClickListener(v ->
            startActivity(new Intent(this, ProfilActivity.class)));
    }
}
