package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.LastMessage;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    public interface OnChatClickListener {
        void onClick(LastMessage chat);
    }

    List<LastMessage> chatList;
    OnChatClickListener listener;

    public ChatListAdapter(List<LastMessage> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {
        LastMessage item = chatList.get(position);
        holder.name.setText(item.chatWithName);
        holder.lastMessage.setText(item.lastMessage);

        String timeAgo = android.text.format.DateUtils.getRelativeTimeSpanString(item.timestamp).toString();
        holder.time.setText(timeAgo);

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, lastMessage, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            lastMessage = itemView.findViewById(R.id.tvLastMessage);
            time = itemView.findViewById(R.id.tvTime);
        }
    }
}
