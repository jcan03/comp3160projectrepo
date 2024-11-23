package com.example.comp3160project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    // initialize list for chat messages
    private List<ChatMessageModel> chatMessages;

    // default constructor for chat adapter
    public ChatAdapter(List<ChatMessageModel> chatMessages)
    {
        this.chatMessages = chatMessages;
    }

    // inflate view holder for each chat item using the single item layout
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    // bind all fields required for each chat message
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessageModel message = chatMessages.get(position);
        holder.usernameTextView.setText(message.getUsername());
        holder.messageTextView.setText(message.getMessage());
        holder.timeSentTextView.setText(message.getFormattedTimestamp());
    }

    // override get item count method with size of list
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // view holder for chat item
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, messageTextView, timeSentTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timeSentTextView = itemView.findViewById(R.id.timeSentTextView);
        }
    }
}
