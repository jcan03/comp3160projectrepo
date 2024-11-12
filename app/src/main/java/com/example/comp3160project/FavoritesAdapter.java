package com.example.comp3160project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private Context context;
    private List<Restaurant> favorites;

    public FavoritesAdapter(Context context, List<Restaurant> favorites)
    {
        this.favorites = favorites;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.resturaunt_item, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = favorites.get(position);
        holder.name.setText(restaurant.getName());
        holder.street.setText(restaurant.getStreet());
        holder.distance.setText(String.valueOf(restaurant.getDistance()));
    }


    @Override
    public int getItemCount() {
        return favorites.size();
    }
}
