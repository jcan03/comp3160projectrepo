package com.example.comp3160project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference messageRef, userRef;
    private FirebaseUser currentUser;

    private EditText messageField;
    private ImageButton sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessageModel> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // Redirect to LoginActivity if user is not authenticated
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        messageRef = FirebaseDatabase.getInstance().getReference("Messages");
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        messageField = findViewById(R.id.messageField);
        sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        // Set up RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Listen for new messages in the Firebase Realtime Database
        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatMessageModel chatMessage = snapshot.getValue(ChatMessageModel.class);
                    if (chatMessage != null) {
                        chatMessages.add(chatMessage);
                    }
                }
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size()); // Scroll to the latest message
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
            }
        });

        // Send button logic
        sendButton.setOnClickListener(v -> {
            String message = messageField.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(MainActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Fetch username from Firebase and send the message
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.getValue(String.class);
                    if (username != null) {
                        sendMessage(username, message);
                    } else {
                        Toast.makeText(MainActivity.this, "Unable to fetch username", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, "Error fetching username", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void sendMessage(String username, String message) {
        long timestamp = System.currentTimeMillis();
        ChatMessageModel chatMessage = new ChatMessageModel(username, message, timestamp);

        // Push message to Firebase
        messageRef.push().setValue(chatMessage).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                messageField.setText("");  // Clear the input field after sending the message
            } else {
                Toast.makeText(MainActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
