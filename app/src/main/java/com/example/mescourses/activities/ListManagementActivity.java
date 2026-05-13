package com.example.mescourses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mescourses.R;
import com.example.mescourses.adapters.ShoppingListAdapter;
import com.example.mescourses.database.DatabaseHelper;
import com.example.mescourses.models.ShoppingList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShoppingListAdapter adapter;
    private DatabaseHelper db;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_management);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        db = DatabaseHelper.getInstance(this);
        recyclerView = findViewById(R.id.rvLists);
        fab = findViewById(R.id.fabAddList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadLists();

        fab.setOnClickListener(v -> showAddListDialog());
    }

    private void loadLists() {
        List<ShoppingList> list = db.getShoppingLists(0); // 0 = active
        if (adapter == null) {
            adapter = new ShoppingListAdapter(list, sl -> {
                Intent intent = new Intent(this, CoursesActivity.class);
                intent.putExtra("list_id", sl.getId());
                intent.putExtra("list_nom", sl.getNom());
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(list);
        }
    }

    private void showAddListDialog() {
        EditText et = new EditText(this);
        et.setHint("Nom de la liste (ex: Courses Hebdo)");

        new AlertDialog.Builder(this)
            .setTitle("Nouvelle Liste")
            .setView(et)
            .setPositiveButton("Créer", (d, w) -> {
                String name = et.getText().toString().trim();
                if (!name.isEmpty()) {
                    db.createShoppingList(name);
                    loadLists();
                } else {
                    Toast.makeText(this, "Le nom est requis", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Annuler", null)
            .show();
    }
}
