package com.example.comp3160project;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    public ImageView restaurantImg;
    public TextView name, street, distance, rating;
    public Button shareButton;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        restaurantImg = itemView.findViewById(R.id.restaurantImage);
        name = itemView.findViewById(R.id.name);
        street = itemView.findViewById(R.id.street);
        distance = itemView.findViewById(R.id.distance);
        rating = itemView.findViewById(R.id.rating);
        shareButton = itemView.findViewById(R.id.shareButton);
    }
}
