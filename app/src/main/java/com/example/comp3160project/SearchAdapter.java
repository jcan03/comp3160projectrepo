package com.example.comp3160project;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comp3160project.R;
import com.example.comp3160project.Restaurant;
import com.example.comp3160project.RestaurantViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RestaurantViewHolder> implements Filterable {

    // declare variables
    private Context context;
    private List<Restaurant> restaurants;
    private List<Restaurant> restaurantsFull; // List for holding the original data
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean searched = false;

    // constructor for search adapter
    public SearchAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
        this.restaurantsFull = new ArrayList<>(restaurants); // get a copy of the original list
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_resturaunt_view, parent, false);
        return new RestaurantViewHolder(view);
    }

    // override view holder for displaying single recycler view item restaurant info
    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.name.setText(restaurant.getName());
        holder.street.setText(restaurant.getStreet());
        holder.rating.setText(restaurant.getRating() + "★");

        // load the image using glide library
        Glide.with(holder.itemView.getContext())
                .load(restaurant.getImageUrl())
                .into(holder.restaurantImg);

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
        holder.favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite(restaurant, holder);
            }
        });
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
                Toast.makeText(context, "There was a database error when trying to retrieve your favourites", Toast.LENGTH_LONG).show();
            }
        });
    }

    // override count method by returning size of array list
    @Override
    public int getItemCount() {
        return restaurants.size();
    }

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

                    // play animation after unfavouriting
                    Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext()
                            , R.anim.heart_like);
                    holder.favouriteButton.startAnimation(animation);

                } else {
                    // if it's not a favorite, add it
                    favoriteRef.setValue(true);
                    holder.favouriteButton.setImageResource(R.drawable.heart_full);

                    // play animation after favouriting
                    Animation animation = AnimationUtils.loadAnimation(context.getApplicationContext()
                            , R.anim.heart_like);
                    holder.favouriteButton.startAnimation(animation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "There was an error when trying to add or remove your favourite restaurant selection", Toast.LENGTH_LONG).show();
            }
        });
    }

    // filter used when user starts typing in search bar
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Restaurant> filteredList = new ArrayList<>();
                if (!searched) { //wait until the user has searched before finalizing data
                    restaurantsFull = new ArrayList<>(restaurants);
                    searched = true;
                }

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

            // override publish results method after filtering
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
