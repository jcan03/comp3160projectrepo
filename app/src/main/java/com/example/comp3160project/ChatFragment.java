package com.example.comp3160project;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private DatabaseReference messageRef, userRef;
    private EditText messageField;
    private ImageButton sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessageModel> chatMessages;
    private Context context;

    // required empty constructor
    public ChatFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;  // Set context when fragment is attached
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize firebase references
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
        messageRef = FirebaseDatabase.getInstance().getReference("Messages");

        // initialize list of messages and adapter
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // set the layout and container to the view
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // initialize layout views
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageField = view.findViewById(R.id.messageField);
        sendButton = view.findViewById(R.id.sendButton);

        // set up recyclerview with layoutmanager and adapter
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        chatRecyclerView.setAdapter(chatAdapter);

        // set up firebase message listener
        if (messageRef != null) {
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
                    chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if (context != null) {  // Ensure context is valid
                        Toast.makeText(context, "Error loading messages: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // send button on click listener
        sendButton.setOnClickListener(v -> {
            String message = messageField.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                if (context != null) {
                    Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            if (userRef != null) {
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.getValue(String.class);
                        if (username != null) {
                            sendMessage(username, message);
                        } else {
                            if (context != null) {
                                Toast.makeText(context, "Unable to fetch username", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (context != null) {
                            Toast.makeText(context, "Error fetching username", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }

    // method to handle sending a message to firebase
    private void sendMessage(String username, String message) {
        long timestamp = System.currentTimeMillis();
        ChatMessageModel chatMessage = new ChatMessageModel(username, message, timestamp);

        // push message to Firebase
        messageRef.push().setValue(chatMessage).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                messageField.setText("");  // clears the send message edit text after sending the message
            } else {
                if (context != null) {
                    Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;  // clear context when fragment is detached from fragment manager
    }
}