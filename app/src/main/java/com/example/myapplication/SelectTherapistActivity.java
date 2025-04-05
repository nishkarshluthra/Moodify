package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.Therapist;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SelectTherapistActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Therapist> therapistList = new ArrayList<>();
    TherapistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_therapist);

        recyclerView = findViewById(R.id.therapistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TherapistAdapter(therapistList, this::openChat);
        recyclerView.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        fetchTherapists();
    }

    private void fetchTherapists() {
        FirebaseFirestore.getInstance().collection("therapists")
                .whereEqualTo("available", true)
                .get()
                .addOnSuccessListener(snapshot -> {
                    therapistList.clear(); // Ensure no duplication
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Therapist therapist = doc.toObject(Therapist.class);
                        Log.d("present","present");
                        if (therapist != null) {
                            therapist.uid = doc.getId(); // Ensure UID is set
                            therapistList.add(therapist);
                        }
                    }
                    adapter.notifyDataSetChanged(); // Refresh UI
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private void openChat(Therapist therapist) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("receiverId", therapist.uid);
        intent.putExtra("receiverName", therapist.name);
        startActivity(intent);
    }
}
