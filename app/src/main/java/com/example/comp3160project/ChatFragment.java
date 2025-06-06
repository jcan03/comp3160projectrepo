package com.example.comp3160project;

import static android.app.Activity.RESULT_OK;
import static android.provider.Settings.ACTION_BLUETOOTH_SETTINGS;
import static android.provider.Settings.ACTION_SETTINGS;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatFragment extends Fragment {

    // declare variables
    private FirebaseAuth auth;
    private DatabaseReference messageRef, userRef;
    private EditText messageField;
    private ImageButton sendButton;
    private ImageView micImage;
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
        this.context = context;  // set context when the fragment is attached
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize firebase references
        auth = FirebaseAuth.getInstance();
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
        micImage = view.findViewById(R.id.microphoneIconChatFrag);

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

                    // only scroll to most recent messages if message exists (avoid null pointer crash if there are no messages)
                    if (chatMessages.size() != 0) {
                        chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                    }
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
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            // variable to retrieve username from model class
                            UserModel user = dataSnapshot.getValue(UserModel.class);

                            if (user != null) {
                                String username = user.getUsername();
                                sendMessage(username, message, auth.getCurrentUser().getEmail());  // we only want to take the username from the user model for messages
                            }
                            else // use anonymous username if user is null for some reason while in chat fragment
                            {
                                sendMessage("Anonymous", message, auth.getCurrentUser().getEmail());
                            }
                        }
                        // provide error message if there is a database error
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            if (context != null) {
                                Toast.makeText(context, "Error getting username", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        // on click of mic image, call start speech recognition method for speech to text
        micImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechRecognition();
            }
        });

        return view;
    }

    // method to handle sending a message to firebase
    private void sendMessage(String username, String message, String email) {
        long timestamp = System.currentTimeMillis();
        ChatMessageModel chatMessage = new ChatMessageModel(username, message, timestamp, email);

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

    // method to allow speech to text functionality to work
    private void startSpeechRecognition() {
        // create an intent for speech recognition
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try
        {
            // launch the speech recognition activity
            speechRecognitionLauncher.launch(intent);
        }
        catch (ActivityNotFoundException e)
        {
            // handle the case where speech recognition is not supported
            Toast.makeText(getContext(), "Speech recognition not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    // launches the activity pop up box for speech to text to work
    private final ActivityResultLauncher<Intent> speechRecognitionLauncher = registerForActivityResult(
            // create a new instance of ActivityResultContracts.StartActivityForResult
            new ActivityResultContracts.StartActivityForResult(),
            // define an anonymous inner class implementing ActivityResultCallback<ActivityResult>
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // check if the result code indicates success and data is not null
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // retrieve the recognized speech results as an ArrayList of strings
                        ArrayList<String> resultData = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (resultData != null && !resultData.isEmpty()) {
                            // display the recognized text in a TextView
                            messageField.setText(resultData.get(0));
                        }
                    }
                }
            }
    );

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;  // clear context when fragment is detached from fragment manager
    }
}