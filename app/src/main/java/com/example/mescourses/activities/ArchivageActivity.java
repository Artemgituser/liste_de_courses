package com.example.mescourses.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mescourses.R;
import com.example.mescourses.adapters.ShoppingListAdapter;
import com.example.mescourses.database.DatabaseHelper;
import com.example.mescourses.models.ShoppingList;

import java.util.List;

public class ArchivageActivity extends AppCompatActivity {

    private RecyclerView      recyclerView;
    private TextView          tvEmpty;
    private ShoppingListAdapter adapter;
    private DatabaseHelper    db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivage);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        db           = DatabaseHelper.getInstance(this);
        recyclerView = findViewById(R.id.recyclerViewArchivage);
        tvEmpty      = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chargerArchivage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerArchivage();
    }

    private void chargerArchivage() {
        List<ShoppingList> list = db.getShoppingLists(1); // 1 = finished

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

        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }
}
