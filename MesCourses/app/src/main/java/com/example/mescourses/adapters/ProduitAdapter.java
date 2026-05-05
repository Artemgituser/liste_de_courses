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

public class ProduitAdapter extends RecyclerView.Adapter<ProduitAdapter.ViewHolder> {

    public interface OnProduitClickListener {
        void onProduitClick(Produit produit, int position);
    }

    public interface OnProduitLongClickListener {
        void onProduitLongClick(Produit produit);
    }

    private List<Produit> produits;
    private OnProduitClickListener listener;
    private OnProduitLongClickListener longClickListener;

    public ProduitAdapter(List<Produit> produits, OnProduitClickListener listener) {
        this.produits = produits;
        this.listener = listener;
    }

    public void setOnLongClickListener(OnProduitLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_produit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Produit p = produits.get(position);
        holder.tvNom.setText(p.getNom());
        holder.tvRayon.setText(p.getRayon());
        holder.tvQuantite.setText("x" + p.getQuantite());

        // Emoji basé sur le rayon
        holder.tvBadge.setText(getRayonEmoji(p.getRayon()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProduitClick(p, position);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onProduitLongClick(p);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() { return produits.size(); }

    public Produit getItem(int position) { return produits.get(position); }

    public void updateList(List<Produit> newList) {
        this.produits = newList;
        notifyDataSetChanged();
    }

    /** Emoji selon le rayon */
    private String getRayonEmoji(String rayon) {
        if (rayon == null) return "🛒";
        String r = rayon.toLowerCase();
        if (r.contains("fruit") || r.contains("légume") || r.contains("legume")) return "🥦";
        if (r.contains("viande") || r.contains("boucherie")) return "🥩";
        if (r.contains("poisson") || r.contains("mer")) return "🐟";
        if (r.contains("boisson") || r.contains("boissons")) return "🥤";
        if (r.contains("laiti") || r.contains("produit lait")) return "🥛";
        if (r.contains("pain") || r.contains("boulangerie")) return "🍞";
        if (r.contains("surgelé") || r.contains("surgele")) return "🧊";
        if (r.contains("hygiène") || r.contains("hygiene") || r.contains("soin")) return "🧴";
        if (r.contains("entretien") || r.contains("nettoyage")) return "🧹";
        if (r.contains("épicerie") || r.contains("epicerie")) return "🫙";
        return "🛒";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvRayon, tvQuantite, tvBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom      = itemView.findViewById(R.id.tvNomProduit);
            tvRayon    = itemView.findViewById(R.id.tvRayon);
            tvQuantite = itemView.findViewById(R.id.tvQuantite);
            tvBadge    = itemView.findViewById(R.id.tvRayonBadge);
        }
    }
}
