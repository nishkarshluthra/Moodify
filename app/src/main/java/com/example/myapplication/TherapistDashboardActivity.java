package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.LastMessage;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class TherapistDashboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<LastMessage> chatList = new ArrayList<>();
    ChatListAdapter adapter;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist_dashboard);

        recyclerView = findViewById(R.id.clientRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatListAdapter(chatList, this::openChat);
        recyclerView.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        currentUserId = FirebaseAuth.getInstance().getUid();
        fetchLastMessages();
    }

    private void fetchLastMessages() {
        FirebaseFirestore.getInstance().collectionGroup("messages")
                .get().addOnSuccessListener(snapshot -> {
                    Map<String, LastMessage> lastMessageMap = new HashMap<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String senderId = doc.getString("senderId");
                        String receiverId = doc.getString("receiverId");
                        String msg = doc.getString("message");
                        long ts = doc.getLong("timestamp");

                        if (senderId == null || receiverId == null) continue;
                        if (!senderId.equals(currentUserId) && !receiverId.equals(currentUserId)) continue;

                        String chatWithId = senderId.equals(currentUserId) ? receiverId : senderId;

                        LastMessage existing = lastMessageMap.get(chatWithId);
                        if (existing == null || ts > existing.timestamp) {
                            lastMessageMap.put(chatWithId, new LastMessage(chatWithId, "", msg, ts));
                        }
                    }

                    fetchUserNamesAndShow(new ArrayList<>(lastMessageMap.values()));
                });
    }

    private void fetchUserNamesAndShow(List<LastMessage> messages) {
        chatList.clear();
        for (LastMessage item : messages) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(item.chatWithId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        User user = snapshot.toObject(User.class);
                        if (user != null) {
                            item.chatWithName = user.name != null ? user.name : user.email;
                            chatList.add(item);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    private void openChat(LastMessage chat) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("receiverId", chat.chatWithId);
        intent.putExtra("receiverName", chat.chatWithName);
        startActivity(intent);
    }
}
