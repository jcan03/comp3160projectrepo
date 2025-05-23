package com.example.comp3160project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    // declare variables
    private List<Restaurant> restaurantList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Context context;

    // constructor for restaurant adapter
    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate the item layout for each restaurant item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_resturaunt_view, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        // get the current Restaurant object
        Restaurant restaurant = restaurantList.get(position);

        // set the values for each view in the ViewHolder
        holder.name.setText(restaurant.getName());
        holder.street.setText(restaurant.getStreet());
        holder.rating.setText(restaurant.getRating() + "★");

        // load the image using glide library
        Glide.with(holder.itemView.getContext())
                .load(restaurant.getImageUrl())
                .into(holder.restaurantImg);

        holder.favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite(restaurant, holder);
            }
        });

        // button on each restaurant item which opens an intent with restaurant information to share
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareText = "Check out this restaurant I found on the Kamloops Local Food Finder app!\n" +
                        "Name: " + restaurant.getName() + "\n" +
                        "Street: " + restaurant.getStreet() + "\n" +
                        "Rating: " + restaurant.getRating();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share restaurant info via"));
            }
        });

        // check if the restaurant is liked
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference favoriteRef = mDatabase.child("UserFavourites").child(userId).child("favourites").child(restaurant.getId());
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // if the restaurant is already a favorite, set the icon to the favorite
                    holder.favouriteButton.setImageResource(R.drawable.heart_full);
                } else {
                    // if it's not a favorite, set the image to empty heart
                    holder.favouriteButton.setImageResource(R.drawable.heart_empty);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "There was a database error while trying to favourite or unfavourite that restaurant", Toast.LENGTH_LONG).show();
            }
        });
    }

    // return the size of the restaurant list
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // method to toggle favourite (favourite or unfavourite restaurants)
    private void toggleFavorite(Restaurant restaurant, RestaurantViewHolder holder) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference favoriteRef = mDatabase.child("UserFavourites").child(userId).child("favourites").child(restaurant.getId());

        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // if the restaurant is already a favorite, remove it
                    favoriteRef.removeValue();
                    holder.favouriteButton.setImageResource(R.drawable.heart_empty);

                    // play animation for favouriting/unfavouriting restaurant
                    Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.heart_like);
                    holder.favouriteButton.startAnimation(animation);

                } else {
                    // if it's not a favorite, add it
                    favoriteRef.setValue(true);
                    holder.favouriteButton.setImageResource(R.drawable.heart_full);

                    // play animation for favouriting/unfavouriting restaurant
                    Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.heart_like);
                    holder.favouriteButton.startAnimation(animation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "There was a database error while trying to update your favourite selection", Toast.LENGTH_LONG).show();
            }
        });
    }
}
