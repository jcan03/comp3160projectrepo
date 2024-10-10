package com.example.comp3160project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailField, passwordField, usernameField;
    private TextView backToLoginTV;
    private Button signUpButton;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // initialize firebase references
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // initialize view variables
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.registerPasswordField);
        usernameField = findViewById(R.id.registerUsernameField);
        signUpButton = findViewById(R.id.signUpButton);
        backToLoginTV = findViewById(R.id.backToLoginTV);

        // sends user back to login page on click of this text view
        backToLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        // on click of sign up button, ensure inputs are filled in, then
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                String username = usernameField.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
                    Toast.makeText(SignUpActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // create a new user with email and password
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    // save username to Firebase Realtime Database under "Users" node
                                    UserModel userModel = new UserModel(username, email);

                                    String userId = user.getUid();
                                    databaseReference.child(userId).setValue(userModel);

                                    Toast.makeText(SignUpActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();

                                    // redirect to MainActivity after successful sign up
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                }
                            } else {
                                Toast.makeText(SignUpActivity.this,  "You must enter a valid Email and a Password with 6+ characters to register", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }
}
