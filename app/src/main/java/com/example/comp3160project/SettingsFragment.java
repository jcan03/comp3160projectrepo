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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {

    private DatabaseReference userRef;
    private Button deleteAccountBtn, logOutBtn;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private Context context;

    public SettingsFragment() {
        // required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;  // Set context when fragment is attached
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize Firebase Auth and get the current user
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // check if the currentUser is null to avoid potential crash
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // initialize button references
        deleteAccountBtn = view.findViewById(R.id.deleteAccountButton);
        logOutBtn = view.findViewById(R.id.logOutButton);

        // set onClickListener for delete account button
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
                    showToast("No user is logged in");
                }
            }
        });

        // on click listener for logging out
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        return view;
    }

    // method to delete account and all associated user data (messages)
    private void deleteAccount() {
        if (currentUser != null && currentUser.getEmail() != null) {
            deleteMessagesByUserEmail(currentUser.getEmail(), () -> {
                // After deleting messages, delete user data and account
                userRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser.delete().addOnCompleteListener(deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                showToast("Account deleted successfully");
                                redirectToLogin();
                            } else {
                                reAuthenticateAndDelete();
                            }
                        });
                    } else {
                        showToast("Failed to delete user data");
                    }
                });
            });
        } else {
            showToast("No user is logged in");
        }
    }

    // method to delete user messages based on email
    private void deleteMessagesByUserEmail(String email, final Runnable onMessagesDeleted) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Messages");

        messagesRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();  // delete each message with the associated account
                }
                onMessagesDeleted.run();  // callback once all messages are deleted
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle database error if needed
            }
        });
    }

    // method to re-authenticate the user and retry account deletion
    private void reAuthenticateAndDelete() {
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

    // method to log out
    private void logOut() {
        if (currentUser != null) {
            FirebaseAuth.getInstance().signOut();
            showToast("User logged out successfully");
            redirectToLogin();
        } else {
            showToast("No user is logged in");
        }
    }

    // utility method to show a toast
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // utility method to redirect to login
    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;  // Clear context when fragment is detached
    }
}
