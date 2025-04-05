package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoodHistoryActivity extends AppCompatActivity {

    private PieChart pieChart;
    private FirebaseFirestore db;
    private String userId;

    private Button btn1Day, btn7Days, btn1Month, btn1Year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_pie);

        pieChart = findViewById(R.id.moodPieChart);
        btn1Day = findViewById(R.id.btn1Day);
        btn7Days = findViewById(R.id.btn7Days);
        btn1Month = findViewById(R.id.btn1Month);
        btn1Year = findViewById(R.id.btn1Year);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btn1Day.setOnClickListener(v -> loadMoodDataForDays(1));
        btn7Days.setOnClickListener(v -> loadMoodDataForDays(7));
        btn1Month.setOnClickListener(v -> loadMoodDataForDays(30));
        btn1Year.setOnClickListener(v -> loadMoodDataForDays(365));

        loadMoodDataForDays(7); // Default: last 7 days
    }

    private void loadMoodDataForDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long todayStart = calendar.getTimeInMillis();
        long startDay = todayStart - ((long) (days - 1)) * 24 * 60 * 60 * 1000;

        db.collection("users")
                .document(userId)
                .collection("moodHistory")
                .whereGreaterThanOrEqualTo("timestamp", startDay)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, Integer> moodCount = new HashMap<>();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String mood = (String) doc.get("mood");
                        Long ts = doc.getLong("timestamp");

                        if (mood != null && ts != null) {
                            // Normalize timestamp to start of its day
                            Calendar entryCal = Calendar.getInstance();
                            entryCal.setTimeInMillis(ts);
                            entryCal.set(Calendar.HOUR_OF_DAY, 0);
                            entryCal.set(Calendar.MINUTE, 0);
                            entryCal.set(Calendar.SECOND, 0);
                            entryCal.set(Calendar.MILLISECOND, 0);

                            long dayKey = entryCal.getTimeInMillis();
                            String combinedKey = mood + "_" + dayKey;

                            // Count per mood per day
                            moodCount.put(combinedKey, moodCount.getOrDefault(combinedKey, 0) + 1);
                        }
                    }

                    // Combine per mood across days
                    Map<String, Integer> totalMoodCounts = new HashMap<>();
                    for (String key : moodCount.keySet()) {
                        String mood = key.split("_")[0];
                        int count = moodCount.get(key);
                        totalMoodCounts.put(mood, totalMoodCounts.getOrDefault(mood, 0) + count);
                    }

                    drawPieChart(totalMoodCounts);
                });
    }

    private void drawPieChart(Map<String, Integer> moodCount) {
        List<PieEntry> entries = new ArrayList<>();
        int[] colors = new int[] {
                Color.parseColor("#4CAF50"), // Happy - Green
                Color.parseColor("#FF9800"), // Neutral - Orange
                Color.parseColor("#F44336"), // Sad - Red
                Color.parseColor("#3F51B5"), // Excited - Blue
        };

        int colorIndex = 0;

        for (Map.Entry<String, Integer> entry : moodCount.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Mood Distribution");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        Description desc = new Description();
        desc.setText("");
        pieChart.setDescription(desc);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.invalidate(); // refresh
    }
}
