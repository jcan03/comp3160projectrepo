package com.example.comp3160project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Ensure to add Glide dependency
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private final Context context;
    private final List<Restaurant> favorites;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public FavoritesAdapter(Context context, List<Restaurant> favorites) {
        this.context = context;
        this.favorites = favorites;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_resturaunt_view, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = favorites.get(position);

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

                String shareText = "Check out this restaurant I found on the Kamloops Local Restaurant Finder app!\n" +
                        "Name: " + restaurant.getName() + "\n" +
                        "Street: " + restaurant.getStreet() + "\n" +
                        "Rating: " + restaurant.getRating();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                holder.itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share restaurant info via"));
            }
        });
        //Check if the restaurant is liked
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference favoriteRef = mDatabase.child("UserFavourites").child(userId).child("favourites").child(restaurant.getId());
        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the restaurant is already a favorite, set the icon to the favorite
                    holder.favouriteButton.setImageResource(R.drawable.heart_full);
                } else {
                    // If it's not a favorite, set the image to empty heart
                    holder.favouriteButton.setImageResource(R.drawable.heart_empty);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error
            }
        });

    }
    private void toggleFavorite(Restaurant restaurant, RestaurantViewHolder holder) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference favoriteRef = mDatabase.child("UserFavourites").child(userId).child("favourites").child(restaurant.getId());

        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the restaurant is already a favorite, remove it
                    favoriteRef.removeValue();
                    holder.favouriteButton.setImageResource(R.drawable.heart_empty);
                    Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext()
                            , R.anim.heart_like);
                    holder.favouriteButton.startAnimation(animation);

                } else {
                    // If it's not a favorite, add it
                    favoriteRef.setValue(true);
                    holder.favouriteButton.setImageResource(R.drawable.heart_full);
                    Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext()
                            , R.anim.heart_like);
                    holder.favouriteButton.startAnimation(animation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error
            }
        });

    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }
}
