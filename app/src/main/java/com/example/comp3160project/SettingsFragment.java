package com.example.comp3160project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsFragment extends Fragment {

    private DatabaseReference userRef;
    private Button deleteAccountBtn, logOutBtn;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private Context context;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;  // Set context when fragment is attached
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth and get the current user
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Check if the currentUser is null to avoid potential crash
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize button references
        deleteAccountBtn = view.findViewById(R.id.deleteAccountButton);
        logOutBtn = view.findViewById(R.id.logOutButton);

        // Set onClickListener for delete account button
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser != null) {
                    // Show confirmation dialog before deleting the account
                    new AlertDialog.Builder(context)
                            .setTitle("Delete Account")
                            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                            .setPositiveButton("Yes", (dialogInterface, i) -> deleteAccount())
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    Toast.makeText(context, "No user is logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for log out button
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        return view;
    }

    // Method to delete account
    private void deleteAccount() {
        if (currentUser != null) {
            // Delete the user data from Firebase Realtime Database first
            userRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Now delete the user account from FirebaseAuth
                    currentUser.delete().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Account deleted successfully, show a toast and redirect
                            Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show();

                            // Redirect to login screen after account deletion
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            // If account deletion fails, sign in again to re-authenticate
                            reAuthenticateAndDelete();
                        }
                    });
                } else {
                    Toast.makeText(context, "Failed to delete user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }

    // Re-authenticate the user and retry account deletion
    private void reAuthenticateAndDelete() {
        // Prompt user to re-authenticate if account deletion fails due to recent authentication restrictions
        new AlertDialog.Builder(context)
                .setTitle("Re-authentication Required")
                .setMessage("Please log in again to complete account deletion.")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .show();
    }

    // Method to log out
    private void logOut() {
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut();

            Toast.makeText(context, "User logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to login screen after logging out
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(context, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;  // Clear context when fragment is detached
    }
}