package com.example.mescourses.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mescourses.R;
import com.example.mescourses.models.Produit;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    public interface OnCourseClickListener {
        void onCourseClick(Produit produit, int position);
    }

    private List<Produit> produits;
    private OnCourseClickListener listener;

    public CourseAdapter(List<Produit> produits, OnCourseClickListener listener) {
        this.produits = produits;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produit p = produits.get(position);
        holder.tvNom.setText(p.getNom());
        holder.tvRayon.setText(p.getRayon());
        holder.tvQuantite.setText("x" + p.getQuantite());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCourseClick(p, position);
        });
    }

    @Override
    public int getItemCount() { return produits.size(); }

    public Produit getItem(int position) { return produits.get(position); }

    public void removeItem(int position) {
        produits.remove(position);
        notifyItemRemoved(position);
    }

    public void updateList(List<Produit> newList) {
        this.produits = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvRayon, tvQuantite;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom      = itemView.findViewById(R.id.tvNomProduit);
            tvRayon    = itemView.findViewById(R.id.tvRayon);
            tvQuantite = itemView.findViewById(R.id.tvQuantite);
        }
    }
}
