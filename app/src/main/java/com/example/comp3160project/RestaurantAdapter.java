package com.example.comp3160project;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private List<Restaurant> restaurantList;

    // Constructor for the adapter
    public RestaurantAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the item layout for each restaurant item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resturaunt_item, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        // get the current Restaurant object
        Restaurant restaurant = restaurantList.get(position);

        // set the values for each view in the ViewHolder
        holder.name.setText(restaurant.getName());
        holder.street.setText(restaurant.getStreet());
        holder.distance.setText(restaurant.getDistance());
        holder.rating.setText(String.valueOf(restaurant.getRating()));

        // load the image using glide library
        Glide.with(holder.itemView.getContext())
                .load(restaurant.getImageUrl())
                .into(holder.restaurantImg);

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
    }

    // return the size of the restaurant list
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }
}
