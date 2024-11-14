package com.example.comp3160project;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private FavoritesAdapter favoritesAdapter;
    private List<Restaurant> favorites = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public FavouritesFragment() {
        // required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // fetch favorites for the current user
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference favoritesRef = mDatabase.child("UserFavourites").child(userId).child("favourites");

        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favorites.clear();
                if (dataSnapshot.exists()) {
                    // Fetch each restaurant data in the favorites list
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String restaurantId = snapshot.getKey();
                        fetchRestaurantData(restaurantId, dataSnapshot.getChildrenCount());
                    }
                } else {
                    // No favorites; update the adapter to reflect an empty list
                    favoritesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log error if needed
            }
        });
    }

    private void fetchRestaurantData(String restaurantId, long totalFavoritesCount) {
        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference("restaurants").child(restaurantId);
        restaurantRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        favorites.add(restaurant);
                    }
                }

                // Notify adapter once all data is fetched
                if (favorites.size() == totalFavoritesCount || !dataSnapshot.exists()) {
                    favoritesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to load restaurant data: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        // Set up RecyclerView and Adapter
        favoritesRecyclerView = view.findViewById(R.id.favouritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoritesAdapter = new FavoritesAdapter(getContext(), favorites);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        return view;
    }
}
