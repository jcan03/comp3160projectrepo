package com.example.comp3160project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RestaurantViewHolder> implements Filterable {

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    private OnRestaurantClickListener listener;

    public SearchAdapter(Context context, List<Restaurant> restaurants, OnRestaurantClickListener listener) {
        this.restaurants = restaurants;
        this.listener = listener;
    }

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
        View view = LayoutInflater.from(context).inflate(R.layout.resturaunt_item, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.name.setText(restaurant.getName());
        holder.street.setText(restaurant.getStreet());
        holder.distance.setText(String.valueOf(restaurant.getDistance()));
        holder.rating.setText("★"+ String.valueOf(restaurant.getRating()) + "/5");

        // button on each restaurant item which opens an intent with restaurant information to share
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareText = "Check out this restaurant I found on Kamloops Restaurant Finder app!\n" +
                        "Name: " + restaurant.getName() + "\n" +
                        "Street: " + restaurant.getStreet() + "\n" +
                        "Rating: " + restaurant.getRating();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share restaurant info via"));

            }
        });

        Glide.with(holder.itemView.getContext())
                .load(restaurant.getImageUrl())
                .into(holder.restaurantImg);

        //Restaurant ClickListener
        holder.name.setOnClickListener(view -> {//TODO: Make this use the full item rather than just the title

            MainActivity mainActivity = (MainActivity) context;

            //Create the fragment and pass the parameters to it.
            ResturauntInfoFragment frag = new ResturauntInfoFragment().newInstance(restaurant.getName(), restaurant.getStreet(), restaurant.getDistance());
            mainActivity.loadFragment(frag); // or whatever fragment you need

            Toast.makeText(context, "Restaurant", Toast.LENGTH_SHORT).show(); //TODO: Make this change pages
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
