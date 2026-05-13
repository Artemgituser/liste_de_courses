package com.example.mescourses.activities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mescourses.R;
import com.example.mescourses.adapters.CourseAdapter;
import com.example.mescourses.database.DatabaseHelper;
import com.example.mescourses.models.Produit;

import java.util.List;

public class CoursesActivity extends AppCompatActivity {

    private RecyclerView  recyclerView;
    private TextView      tvEmpty;
    private CourseAdapter adapter;
    private DatabaseHelper db;
    private int           listId;
    private String        listNom;
    private android.widget.Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        listId = getIntent().getIntExtra("list_id", -1);
        listNom = getIntent().getStringExtra("list_nom");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (listNom != null) getSupportActionBar().setTitle(listNom);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        db           = DatabaseHelper.getInstance(this);
        recyclerView = findViewById(R.id.recyclerViewCourses);
        tvEmpty      = findViewById(R.id.tvEmpty);
        btnFinish    = findViewById(R.id.btnFinishTrip);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chargerCourses();
        attachSwipe();

        btnFinish.setOnClickListener(v -> finishTrip());
    }

    @Override
    protected void onResume() {
        super.onResume();
        chargerCourses();
    }

    private void chargerCourses() {
        List<Produit> list;
        if (listId != -1) {
            list = db.getItemsForList(listId);
        } else {
            list = db.getProduitsCourses();
        }

        if (adapter == null) {
            adapter = new CourseAdapter(list, (produit, position) -> {
                // Clic simple : pas d'action spéciale sur les courses
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateList(list);
        }

        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void finishTrip() {
        if (listId != -1) {
            new AlertDialog.Builder(this)
                .setTitle("Terminer les courses")
                .setMessage("Voulez-vous marquer cette liste comme terminée et l'archiver dans l'historique ?")
                .setPositiveButton("Oui", (d, w) -> {
                    db.finishShoppingList(listId);
                    Toast.makeText(this, "Liste terminée !", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Non", null)
                .show();
        } else {
            Toast.makeText(this, "Action impossible sur la liste globale", Toast.LENGTH_SHORT).show();
        }
    }

    private void attachSwipe() {
        ItemTouchHelper.SimpleCallback swipeCallback =
            new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position  = viewHolder.getAdapterPosition();
                Produit produit = adapter.getItem(position);

                if (direction == ItemTouchHelper.LEFT) {
                    // Swipe droite→gauche : supprimer de la liste courses (pas de la base)
                    db.updateStatut(produit.getId(), Produit.STATUT_BASE);
                    adapter.removeItem(position);
                    Toast.makeText(CoursesActivity.this,
                        produit.getNom() + " retiré des courses", Toast.LENGTH_SHORT).show();
                } else {
                    // Swipe gauche→droite : archiver pour la prochaine liste
                    db.updateStatut(produit.getId(), Produit.STATUT_ARCHIVE);
                    adapter.removeItem(position);
                    Toast.makeText(CoursesActivity.this,
                        produit.getNom() + " archivé pour la prochaine fois",
                        Toast.LENGTH_SHORT).show();
                }

                // Afficher message vide si plus d'articles
                tvEmpty.setVisibility(adapter.getItemCount() == 0
                    ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(adapter.getItemCount() == 0
                    ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                View itemView = viewHolder.itemView;
                Paint paint   = new Paint();
                Paint textPaint = new Paint();
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(42f);
                textPaint.setAntiAlias(true);

                float itemHeight = itemView.getBottom() - itemView.getTop();
                float cornerRadius = 16f;

                if (dX > 0) {
                    // Swipe vers droite → vert "Archiver"
                    paint.setColor(Color.parseColor("#4CAF50"));
                    RectF background = new RectF(
                        itemView.getLeft(), itemView.getTop(),
                        itemView.getLeft() + dX, itemView.getBottom());
                    c.drawRoundRect(background, cornerRadius, cornerRadius, paint);
                    c.drawText("← Archiver",
                        itemView.getLeft() + 30f,
                        itemView.getTop() + itemHeight / 2f + 15f, textPaint);

                } else if (dX < 0) {
                    // Swipe vers gauche → rouge "Supprimer de la liste"
                    paint.setColor(Color.parseColor("#D64B3A"));
                    RectF background = new RectF(
                        itemView.getRight() + dX, itemView.getTop(),
                        itemView.getRight(), itemView.getBottom());
                    c.drawRoundRect(background, cornerRadius, cornerRadius, paint);
                    String label = "Retirer →";
                    float textWidth = textPaint.measureText(label);
                    c.drawText(label,
                        itemView.getRight() - textWidth - 30f,
                        itemView.getTop() + itemHeight / 2f + 15f, textPaint);
                }

                super.onChildDraw(c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
    }
}
