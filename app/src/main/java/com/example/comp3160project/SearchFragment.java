package com.example.comp3160project;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference mDatabase;
// ...

    private RecyclerView searchRecyclerView;
    private SearchView restaurantSearch;
    private List<Restaurant> restaurants;
    private SearchAdapter searchAdapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);

                    if (restaurants != null) {
                        restaurants.add(restaurant);
                    }
                }
                searchAdapter.notifyDataSetChanged();
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.child("restaurants").addValueEventListener(postListener);


        restaurants = new ArrayList<>();
        //TODO: make this actually pull stuff from firebase


        searchAdapter = new SearchAdapter(getContext(), restaurants);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //setup recyclerview and the searchview
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);
        restaurantSearch = view.findViewById(R.id.searchView);

        //set the Recyclerview adapter
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(searchAdapter);


        restaurantSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //activated when they hit submit
                searchAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //activated when they are typing
                searchAdapter.getFilter().filter(newText);
                return false;
            }
        });



        return view;
    }
    public void writeNewRestaurant(Restaurant restaurant)
    {
        mDatabase.child("restaurants").push().setValue(restaurant);
    }
}


