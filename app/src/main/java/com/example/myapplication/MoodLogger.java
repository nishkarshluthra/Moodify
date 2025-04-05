package com.example.myapplication.repository;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MoodLogger {

    private static final String TAG = "MoodLogger";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void logMood(String mood) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (userId == null) {
            Log.e(TAG, "User not logged in. Cannot save mood.");
            return;
        }

        Map<String, Object> moodData = new HashMap<>();
        moodData.put("mood", mood);
        moodData.put("timestamp", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .collection("moodHistory")
                .add(moodData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Mood saved: " + mood))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save mood", e));
    }
}
