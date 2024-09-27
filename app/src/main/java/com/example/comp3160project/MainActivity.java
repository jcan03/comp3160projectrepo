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

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private ImageButton forYouButton, favouritesButton, searchButton, settingsButton, chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to LoginActivity if the user is not authenticated
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        forYouButton = findViewById(R.id.forYouButton);
        favouritesButton = findViewById(R.id.favouritesButton);
        searchButton = findViewById(R.id.searchButton);
        settingsButton = findViewById(R.id.settingsButton);
        chatButton = findViewById(R.id.chatButton);

        // Load ChatFragment initially
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ChatFragment())
                .commit();

        // Set up button listeners
        chatButton.setOnClickListener(v -> loadFragment(new ChatFragment()));
        forYouButton.setOnClickListener(v -> loadFragment(new ForYouFragment()));
        favouritesButton.setOnClickListener(v -> loadFragment(new FavouritesFragment()));
        searchButton.setOnClickListener(v -> loadFragment(new SearchFragment()));
        settingsButton.setOnClickListener(v -> loadFragment(new SettingsFragment()));
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }
}
