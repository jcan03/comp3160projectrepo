package com.example.comp3160project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // create variables
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private ImageButton forYouButton, favouritesButton, searchButton, settingsButton, chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize firebase variables
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // send user to login activity if there is not anyone logged in
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // set variables to their layout ids
        forYouButton = findViewById(R.id.forYouButton);
        favouritesButton = findViewById(R.id.favouritesButton);
        searchButton = findViewById(R.id.searchButton);
        settingsButton = findViewById(R.id.settingsButton);
        chatButton = findViewById(R.id.chatButton);

        // load the chat fragment initially
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ChatFragment())
                .commit();

        // set up button listeners for each fragment
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ChatFragment());
            }
        });

        forYouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ForYouFragment());
            }
        });

        favouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new FavouritesFragment());
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SearchFragment());
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new SettingsFragment());
            }
        });
    }

    // method to load the fragments, replacing the layout in the fragment container
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }
}
