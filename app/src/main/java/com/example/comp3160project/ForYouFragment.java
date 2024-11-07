package com.example.comp3160project;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForYouFragment extends Fragment {

    private RecyclerView forYouRecyclerView;
    private RestaurantAdapter restaurantAdapter; // Create this adapter
    private List<Restaurant> restaurantList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_you, container, false);

        // Initialize RecyclerView
        forYouRecyclerView = view.findViewById(R.id.forYouRecyclerView);
        forYouRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Adapter
        restaurantAdapter = new RestaurantAdapter(getContext(), restaurantList);
        forYouRecyclerView.setAdapter(restaurantAdapter);

        // call method to randomly select restaurants to show in recycler view
        loadRandomRestaurants();

        return view;
    }

    private void loadRandomRestaurants() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("restaurants");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Restaurant> allRestaurants = new ArrayList<>();

                // pull all restaurants from firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    allRestaurants.add(restaurant);
                }

                // shuffle the restaurants and randomly display 3 restaurants in the recycler view
                Collections.shuffle(allRestaurants);
                List<Restaurant> randomRestaurants = allRestaurants.subList(0, Math.min(3, allRestaurants.size()));

                // clear current list and update RecyclerView
                restaurantList.clear();
                restaurantList.addAll(randomRestaurants);
                restaurantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "There was an error retrieving the For You page information", Toast.LENGTH_LONG).show();
            }
        });
    }
}
