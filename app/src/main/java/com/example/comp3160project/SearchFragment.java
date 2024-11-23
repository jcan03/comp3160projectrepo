package com.example.comp3160project;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    // declare variables
    private DatabaseReference mDatabase;
    private RecyclerView searchRecyclerView;
    private SearchView restaurantSearch;
    private List<Restaurant> restaurants;
    private SearchAdapter searchAdapter;
    private FirebaseAuth mAuth;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // initialize restaurant list and adapter
        restaurants = new ArrayList<>();
        searchAdapter = new SearchAdapter(getContext(), restaurants);

        // call method to load restaurants from Firebase
        loadRestaurantsFromFirebase();
    }

    // method that loads all of the restaurants from firebase and displays them in the recyclerview in search fragment
    private void loadRestaurantsFromFirebase() {
        mDatabase.child("restaurants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                restaurants.clear(); // clear existing data to avoid duplicates in recycler view
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        restaurants.add(restaurant);
                    }
                }
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // error
            }
        });
    }

    // override and inflate view for search fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // setup RecyclerView and SearchView
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        restaurantSearch = view.findViewById(R.id.searchView);

        // configure RecyclerView with adapter
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(searchAdapter);

        // setup search filtering
        restaurantSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }
}
