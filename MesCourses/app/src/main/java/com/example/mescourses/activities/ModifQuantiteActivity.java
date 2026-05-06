package com.example.mescourses.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.mescourses.R;
import com.example.mescourses.database.DatabaseHelper;
import com.example.mescourses.models.Category;
import com.example.mescourses.models.Produit;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class ModifQuantiteActivity extends AppCompatActivity {

    private TextInputLayout   tilNom, tilQuantite, tilRayon, tilCategory;
    private TextInputEditText etNom, etQuantite, etRayon;
    private AutoCompleteTextView actvCategory;
    private Button            btnSave;
    private DatabaseHelper    db;
    private int               produitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modif_quantite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        db          = DatabaseHelper.getInstance(this);
        tilNom       = findViewById(R.id.tilNom);
        tilQuantite  = findViewById(R.id.tilQuantite);
        tilRayon     = findViewById(R.id.tilRayon);
        tilCategory  = findViewById(R.id.tilCategory);
        etNom        = findViewById(R.id.etNom);
        etQuantite   = findViewById(R.id.etQuantite);
        etRayon      = findViewById(R.id.etRayon);
        actvCategory = findViewById(R.id.actvCategory);
        btnSave      = findViewById(R.id.btnSave);

        // Récupération de l'id passé par l'intent
        produitId = getIntent().getIntExtra("produit_id", -1);

        if (produitId == -1) {
            Toast.makeText(this, "Produit introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Pré-remplir les champs
        Produit produit = db.getProduitById(produitId);
        if (produit == null) {
            Toast.makeText(this, "Produit introuvable", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etNom.setText(produit.getNom());
        etQuantite.setText(String.valueOf(produit.getQuantite()));
        etRayon.setText(produit.getRayon());

        // Setup category spinner
        List<Category> categories = db.getAllCategories();
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);

        // Find and set current category
        for (Category c : categories) {
            if (c.getId() == produit.getCategoryId()) {
                actvCategory.setText(c.getNom(), false);
                break;
            }
        }

        btnSave.setOnClickListener(v -> sauvegarder());
    }

    private void sauvegarder() {
        String nom   = etNom.getText()      != null ? etNom.getText().toString().trim()      : "";
        String qStr  = etQuantite.getText() != null ? etQuantite.getText().toString().trim() : "";
        String rayon = etRayon.getText()    != null ? etRayon.getText().toString().trim()    : "";
        String categoryName = actvCategory.getText().toString().trim();

        boolean valid = true;

        if (nom.isEmpty()) {
            tilNom.setError("Nom requis");
            valid = false;
        } else {
            tilNom.setError(null);
        }

        if (qStr.isEmpty()) {
            tilQuantite.setError("Quantité requise");
            valid = false;
        } else {
            tilQuantite.setError(null);
        }

        if (rayon.isEmpty()) {
            tilRayon.setError("Rayon requis");
            valid = false;
        } else {
            tilRayon.setError(null);
        }

        if (!valid) return;

        try {
            int quantite = Integer.parseInt(qStr);
            if (quantite <= 0) throw new NumberFormatException();

            int categoryId = -1;
            if (!categoryName.isEmpty()) {
                categoryId = (int) db.insertCategory(categoryName);
            }

            db.updateProduit(produitId, nom, quantite, rayon, categoryId);
            Toast.makeText(this, "✓ Produit mis à jour", Toast.LENGTH_SHORT).show();
            finish();

        } catch (NumberFormatException e) {
            tilQuantite.setError("Quantité invalide (entier > 0)");
        }
    }
}
