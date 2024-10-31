package com.example.comp3160project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForYouFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResturauntInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_NAME = "param1";
    private static final String ARG_DESCRIPTION = "param2";
    private static final String ARG_LOCATION = "param3";

    // TODO: Rename and change types of parameters
    private String mTitle;
    private String mDescription;
    private String mLocation;

    private TextView title, description, location;

    public ResturauntInfoFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param name Parameter 1.
     * @param description Parameter 2.
     * @return A new instance of fragment ResturauntInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResturauntInfoFragment newInstance(String name, String description, String location) {
        ResturauntInfoFragment fragment = new ResturauntInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_NAME);
            mDescription = getArguments().getString(ARG_DESCRIPTION);
            mLocation = getArguments().getString(ARG_NAME);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_resturaunt_info, container, false);

        //get the text views
        title = view.findViewById(R.id.restInfoName);
        description = view.findViewById(R.id.restInfoDescription);
        location = view.findViewById(R.id.restInfoLocation);

        //set the text view names
        title.setText(mTitle);
        description.setText(mDescription);
        location.setText(mLocation);

        return view;
    }
}