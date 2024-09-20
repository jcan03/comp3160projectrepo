package com.example.comp3160project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailField, passwordField, usernameField;
    private Button signUpButton;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        usernameField = findViewById(R.id.usernameField);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String username = usernameField.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
                Toast.makeText(SignUpActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new user with email and password
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Save username to Firebase Realtime Database under "Users" node
                                String userId = user.getUid();
                                databaseReference.child(userId).setValue(username);

                                Toast.makeText(SignUpActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();

                                // Redirect to MainActivity after successful sign up
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
