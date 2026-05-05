package com.example.mescourses.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.mescourses.R;
import com.example.mescourses.adapters.ProduitAdapter;
import com.example.mescourses.database.DatabaseHelper;
import com.example.mescourses.models.Category;
import com.example.mescourses.models.Produit;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ProduitsActivity extends AppCompatActivity {

    private TextInputLayout   tilNom, tilQuantite, tilRayon, tilCategory;
    private TextInputEditText etNom, etQuantite, etRayon, etSearch;
    private AutoCompleteTextView actvCategory;
    private Button            btnAjouter;
    private RecyclerView      recyclerView;
    private TextView          tvCount;

    private DatabaseHelper    db;
    private ProduitAdapter    adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produits);

        // Toolbar avec bouton retour
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db           = DatabaseHelper.getInstance(this);
        tilNom       = findViewById(R.id.tilNom);
        tilQuantite  = findViewById(R.id.tilQuantite);
        tilRayon     = findViewById(R.id.tilRayon);
        tilCategory  = findViewById(R.id.tilCategory);
        etNom        = findViewById(R.id.etNom);
        etQuantite   = findViewById(R.id.etQuantite);
        etRayon      = findViewById(R.id.etRayon);
        etSearch     = findViewById(R.id.etSearch);
        actvCategory = findViewById(R.id.actvCategory);
        btnAjouter   = findViewById(R.id.btnAjouter);
        recyclerView = findViewById(R.id.recyclerViewProduits);
        tvCount      = findViewById(R.id.tvCount);

        // Setup category suggestions
        updateCategorySpinner();

        // RecyclerView setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chargerProduits();

        btnAjouter.setOnClickListener(v -> ajouterProduit());

        // Setup search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                rechercherProduits(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void updateCategorySpinner() {
        List<Category> categories = db.getAllCategories();
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvCategory.setAdapter(adapter);
    }

    private void rechercherProduits(String query) {
        if (query.isEmpty()) {
            chargerProduits();
            return;
        }
        List<Produit> list = db.searchProduits(query);
        adapter.updateList(list);
        tvCount.setText(list.size() + " produit(s) trouvé(s)");
    }

    private void chargerProduits() {
        List<Produit> list = db.getAllProduits();

        if (adapter == null) {
            adapter = new ProduitAdapter(list, (produit, position) -> {
                // Clic sur un item → AlertDialog pour modifier ou supprimer
                showOptionsDialog(produit);
            });
            adapter.setOnLongClickListener(produit -> {
                showAddToListDialog(produit);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(list);
        }

        tvCount.setText(list.size() + " produit(s)");
    }

    private void ajouterProduit() {
        String nom   = etNom.getText() != null ? etNom.getText().toString().trim() : "";
        String qStr  = etQuantite.getText() != null ? etQuantite.getText().toString().trim() : "";
        String rayon = etRayon.getText() != null ? etRayon.getText().toString().trim() : "";
        String categoryName = actvCategory.getText().toString().trim();

        boolean valid = true;

        if (nom.isEmpty()) {
            tilNom.setError("Le nom est requis");
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
            tilRayon.setError("Le rayon est requis");
            valid = false;
        } else {
            tilRayon.setError(null);
        }

        if (!valid) return;

        int quantite;
        try {
            quantite = Integer.parseInt(qStr);
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            tilQuantite.setError("Quantité invalide (nombre > 0)");
            return;
        }

        int categoryId = -1;
        if (!categoryName.isEmpty()) {
            categoryId = (int) db.insertCategory(categoryName);
            updateCategorySpinner();
        }

        long result = db.insertProduit(nom, quantite, rayon, categoryId);

        if (result == -1) {
            // Doublon détecté → AlertDialog
            new AlertDialog.Builder(this)
                .setTitle("Produit existant")
                .setMessage("\"" + nom + "\" dans le rayon \"" + rayon + "\" existe déjà.")
                .setPositiveButton("OK", null)
                .show();
        } else {
            Toast.makeText(this, "✓ Produit ajouté !", Toast.LENGTH_SHORT).show();
            etNom.setText("");
            etQuantite.setText("");
            etRayon.setText("");
            actvCategory.setText("");
            chargerProduits();
        }
    }

    private void showOptionsDialog(Produit produit) {
        String[] options = {"Modifier", "Supprimer", "Annuler"};

        new AlertDialog.Builder(this)
            .setTitle(produit.getNom())
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // Modifier
                        showModifDialog(produit);
                        break;
                    case 1: // Supprimer
                        new AlertDialog.Builder(this)
                            .setTitle("Supprimer")
                            .setMessage("Supprimer \"" + produit.getNom() + "\" définitivement ?")
                            .setPositiveButton("Supprimer", (d, w) -> {
                                db.deleteProduit(produit.getId());
                                Toast.makeText(this, "Produit supprimé", Toast.LENGTH_SHORT).show();
                                chargerProduits();
                            })
                            .setNegativeButton("Annuler", null)
                            .show();
                        break;
                }
            }).show();
    }

    private void showAddToListDialog(Produit produit) {
        List<com.example.mescourses.models.ShoppingList> lists = db.getShoppingLists(0);
        if (lists.isEmpty()) {
            Toast.makeText(this, "Créez d'abord une liste de courses", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] listNames = new String[lists.size()];
        for (int i = 0; i < lists.size(); i++) {
            listNames[i] = lists.get(i).getNom();
        }

        new AlertDialog.Builder(this)
            .setTitle("Ajouter à une liste")
            .setItems(listNames, (dialog, which) -> {
                db.addItemToList(lists.get(which).getId(), produit.getId(), 1);
                Toast.makeText(this, "Ajouté à " + listNames[which], Toast.LENGTH_SHORT).show();
            })
            .show();
    }

    private void showModifDialog(Produit produit) {
        android.view.View v = getLayoutInflater().inflate(R.layout.activity_modif_quantite, null);
        TextInputEditText etN = v.findViewById(R.id.etNom);
        TextInputEditText etQ = v.findViewById(R.id.etQuantite);
        TextInputEditText etR = v.findViewById(R.id.etRayon);
        AutoCompleteTextView actvC = v.findViewById(R.id.actvCategory);

        etN.setText(produit.getNom());
        etQ.setText(String.valueOf(produit.getQuantite()));
        etR.setText(produit.getRayon());

        // Setup category spinner in dialog
        List<Category> categories = db.getAllCategories();
        ArrayAdapter<Category> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        actvC.setAdapter(catAdapter);

        // Find and set current category
        for (Category c : categories) {
            if (c.getId() == produit.getCategoryId()) {
                actvC.setText(c.getNom(), false);
                break;
            }
        }

        new AlertDialog.Builder(this)
            .setTitle("Modifier le produit")
            .setView(v)
            .setPositiveButton("Enregistrer", (d, w) -> {
                String newNom   = etN.getText() != null ? etN.getText().toString().trim() : "";
                String newQ     = etQ.getText() != null ? etQ.getText().toString().trim() : "";
                String newRayon = etR.getText() != null ? etR.getText().toString().trim() : "";
                String newCat   = actvC.getText().toString().trim();

                if (newNom.isEmpty() || newQ.isEmpty() || newRayon.isEmpty()) {
                    Toast.makeText(this, "Tous les champs sont requis", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    int q = Integer.parseInt(newQ);
                    int catId = -1;
                    if (!newCat.isEmpty()) {
                        catId = (int) db.insertCategory(newCat);
                    }
                    db.updateProduit(produit.getId(), newNom, q, newRayon, catId);
                    Toast.makeText(this, "Produit modifié", Toast.LENGTH_SHORT).show();
                    updateCategorySpinner();
                    chargerProduits();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Quantité invalide", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Annuler", null)
            .show();
    }
}
