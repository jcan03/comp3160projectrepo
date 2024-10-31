package com.example.comp3160project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment {


    private TextView title; //remove this at the end of the day if this isn't being used
    private RecyclerView favoritesRecyclerView;

    private SearchAdapter favoritesAdapter;
    private List<Restaurant> favorites;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavouritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouritesFragment newInstance(String param1, String param2) {
        FavouritesFragment fragment = new FavouritesFragment();
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


        favorites = new ArrayList<Restaurant>();
        favorites.add(new Restaurant("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSa3pIdeo1ItkpL03CbhvRB8I5E2SlFGdrDoA&s",
                "Kochi Bao",
                "In your area",
                50,
                4.5
        ));
        favorites.add(new Restaurant("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSa3pIdeo1ItkpL03CbhvRB8I5E2SlFGdrDoA&s",
                "Tiger Ramen",
                "Seymour on the Street",
                50,
                4.8
        ));

        favorites.add(new Restaurant("https://lixil.cdn.celum.cloud/71295_B-2794204020_CDNwebp.webp",
                "Toilet",
                "Old main",
                50,
                4.5
        ));

        favorites.add(new Restaurant("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSa3pIdeo1ItkpL03CbhvRB8I5E2SlFGdrDoA&s",
                "Awesome food",
                "gave me cancer tho :(",
                50,
                6.0
        ));

        favoritesAdapter = new SearchAdapter(getContext(), favorites);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        favoritesRecyclerView = view.findViewById(R.id.favouritesRecyclerView);

        favoritesRecyclerView.setAdapter(favoritesAdapter);

        return view;
    }
}