package com.example.mescourses.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mescourses.R;
import com.example.mescourses.models.ShoppingList;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ListViewHolder> {

    private List<ShoppingList> list;
    private OnListClickListener listener;

    public interface OnListClickListener {
        void onListClick(ShoppingList shoppingList);
    }

    public ShoppingListAdapter(List<ShoppingList> list, OnListClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateList(List<ShoppingList> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list, parent, false);
        return new ListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        ShoppingList sl = list.get(position);
        holder.tvName.setText(sl.getNom());
        holder.tvDate.setText("Créé le : " + sl.getDateCreation());
        holder.itemView.setOnClickListener(v -> listener.onListClick(sl));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDate;
        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvListName);
            tvDate = itemView.findViewById(R.id.tvListDate);
        }
    }
}
