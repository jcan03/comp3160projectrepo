package com.example.comp3160project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp3160project.R;
import com.example.comp3160project.Restaurant;
import com.example.comp3160project.RestaurantViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RestaurantViewHolder> implements Filterable {

    private Context context;
    private List<Restaurant> restaurants;
    private List<Restaurant> restaurantsFull; // List for holding the original data

    public SearchAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
        this.restaurantsFull = new ArrayList<>(restaurants); // Copy of the original list
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_resturaunt_view, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.name.setText(restaurant.getName());
        holder.street.setText(restaurant.getStreet());
        holder.distance.setText(String.valueOf(restaurant.getDistance()));

        //Restaurant ClickListener
        holder.name.setOnClickListener(view -> {//TODO: Make this use the full item rather than just the title
            Toast.makeText(context, "Resturaunt", Toast.LENGTH_SHORT).show(); //TODO: Make this change pages

        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Restaurant> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(restaurantsFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Restaurant restaurant : restaurantsFull) {
                        if (restaurant.getName().toLowerCase().contains(filterPattern) ||
                                restaurant.getStreet().toLowerCase().contains(filterPattern)) {
                            filteredList.add(restaurant);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                restaurants.clear();
                restaurants.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
