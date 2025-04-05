package com.example.myapplication;
import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.Puzzle;
import com.example.myapplication.model.PuzzleManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.example.myapplication.SharedPreferencesHelper;
import com.example.myapplication.UpdateDayReceiver;
import com.example.myapplication.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = "MainActivity";

    // UI Elements
    private TextView userName, resultText,selectedMoodText;
    private Button logout, analyzeButton;
    private EditText inputText;
    ImageButton btnDashboardToggle;
    LinearLayout dashboardPanel;
    TextView navHome, navSupport, navJournal, navLogout;
    private Button btnShowPuzzles;
    // Puzzle Management
    private int currentPuzzleIndex = 0;
    private List<Puzzle> currentPuzzleList = new ArrayList<>();
    private String currentMood = "";
//
    private TextView btnJournal, btnJournalHistory;

    private ImageView moodImageView;
    private ImageButton moodHistoryButton, addCommentButton, shareAppButton, mood_disapButton, mood_sadImageButton, mood_happyImageView,mood_neutralImageView,mood_shappyImageView;
//    private RelativeLayout parentRelativeLayout;

    // Google Sign-In
    private GoogleSignInClient gClient;
    private GoogleSignInOptions gOptions;

    // Mood Tracker
    private GestureDetector mDetector;
    private SharedPreferences mPreferences;
    private int currentDay, currentMoodIndex;
    private String currentComment;
    private View dashboardOverlay;

    private TextView playlistTextView;
    private FirebaseAnalytics mFirebaseAnalytics;
    private List<Playlist> playlists;
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ImageView capturedImageView;
    private ProcessCameraProvider cameraProvider;
    private Button btnRetake,btnShowSongs,btnCapture;
    LinearLayout navJournalLayout, journalDropdown;

    ImageView navJournalArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewView = findViewById(R.id.previewView);
        capturedImageView = findViewById(R.id.capturedImageView);
        btnDashboardToggle = findViewById(R.id.btnDashboardToggle);
        dashboardPanel = findViewById(R.id.dashboardPanel);
        dashboardOverlay = findViewById(R.id.dashboardOverlay);


        navHome = findViewById(R.id.navHome);
        navSupport = findViewById(R.id.navSupport);
        navJournal = findViewById(R.id.navJournal);
        navLogout = findViewById(R.id.navLogout);

        btnCapture = findViewById(R.id.btnCapture);
        btnShowPuzzles = findViewById(R.id.btnShowPuzzles);
        btnShowPuzzles.setVisibility(View.GONE);
        btnShowPuzzles.setOnClickListener(v -> {
            startPuzzleSession();
        });
        navJournalLayout = findViewById(R.id.navJournalLayout);
        journalDropdown = findViewById(R.id.journalDropdown);
        btnJournal = findViewById(R.id.btnjournal);
        btnJournalHistory = findViewById(R.id.btnJournalHistory);

// Set click listeners
        btnJournal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JournalEntryActivity.class);
            startActivity(intent);
        });

        btnJournalHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JournalCalendarActivity.class);
            startActivity(intent);
        });
        navJournalArrow = findViewById(R.id.navJournalArrow);
        navJournalLayout.setOnClickListener(v -> {
            if (journalDropdown.getVisibility() == View.VISIBLE) {
                journalDropdown.setVisibility(View.GONE);
                navJournalArrow.setImageResource(R.drawable.baseline_arrow_right_24);
            } else {
                journalDropdown.setVisibility(View.VISIBLE);
                navJournalArrow.setImageResource(R.drawable.baseline_arrow_drop_down_24);
            }
        });




        playlists = new ArrayList<>();
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        btnCapture.setOnClickListener(v -> takePhoto());
        btnDashboardToggle.setOnClickListener(v -> {
                    if (dashboardPanel.getVisibility() == View.VISIBLE) {
                        // Hide the dashboard
                        dashboardPanel.setVisibility(View.GONE);
                        dashboardOverlay.setVisibility(View.GONE);
                    } else {
                        // Show the dashboard
                        dashboardPanel.setVisibility(View.VISIBLE);
                        dashboardOverlay.setVisibility(View.VISIBLE);
                    }
                });
        navHome.setOnClickListener(v -> {
            dashboardPanel.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
            // Optional: Scroll to top or perform other home actions
        });


        navSupport.setOnClickListener(v -> {
            dashboardPanel.setVisibility(View.GONE);
            Intent intent = new Intent(MainActivity.this, SelectTherapistActivity.class);
            startActivity(intent);
        });



        navLogout.setOnClickListener(v -> {
            dashboardPanel.setVisibility(View.GONE);
            gClient.signOut().addOnCompleteListener(task -> {
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            });
        });


        Log.d(TAG, "onCreate: MainActivity started");

        // Initialize UI

        logout = findViewById(R.id.logout);
//
        resultText = findViewById(R.id.resultText);
//
        addCommentButton = findViewById(R.id.btn_add_comment);
        moodHistoryButton = findViewById(R.id.btn_mood_history);
        shareAppButton = findViewById(R.id.shareButton);
        btnRetake = findViewById(R.id.btnRetake);
        btnShowSongs=findViewById(R.id.btnShowSongs);
//        playlistTextView = findViewById(R.id.playlistTextView);

        btnRetake.setOnClickListener(v -> restartCamera());
        btnShowSongs.setOnClickListener(v -> {
            // Start Songs Activity
            if (playlists != null && !playlists.isEmpty()) {
                Intent intent = new Intent(MainActivity.this, SongsActivity.class);
                intent.putParcelableArrayListExtra("playlists", new ArrayList<>(playlists));  // Pass the playlists
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "No playlists available", Toast.LENGTH_SHORT).show();
            }

        });

        // Google Sign-In
        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);
        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null) {
            String gName = gAccount.getDisplayName();
            userName.setText(gName);
        }

        // Logout Functionality
        logout.setOnClickListener(view -> gClient.signOut().addOnCompleteListener(task -> {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }));

        // Initialize Gesture Detector & Preferences
        mDetector = new GestureDetector(this, this);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Load Mood Data
        currentDay = mPreferences.getInt(SharedPreferencesHelper.KEY_CURRENT_DAY, 1);
        currentMoodIndex = mPreferences.getInt(SharedPreferencesHelper.KEY_CURRENT_MOOD, 3);
        currentComment = mPreferences.getString(SharedPreferencesHelper.KEY_CURRENT_COMMENT, "");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        changeUiForMood(currentMoodIndex);
        scheduleAlarm();

        // Emotion Analysis Button
//        analyzeButton.setOnClickListener(view -> {
//            String text = inputText.getText().toString();
//            if (text.isEmpty()) {
//                resultText.setText("Please enter some text!");
//                return;
//            }
//            analyzeEmotion(text);
//        });

        // Add Comment Dialog
        addCommentButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final EditText editText = new EditText(MainActivity.this);
            builder.setMessage("Add your comment")
                    .setView(editText)
                    .setPositiveButton("Confirm", (dialog, which) -> {
                        if (!editText.getText().toString().isEmpty()) {
                            SharedPreferencesHelper.saveComment(editText.getText().toString(), currentDay, mPreferences);
                        }
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "Comment Saved", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                        Toast.makeText(MainActivity.this, "Comment Canceled", Toast.LENGTH_SHORT).show();
                    })
                    .create().show();
        });

        // Mood History Button
        moodHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MoodHistoryActivity.class);
            startActivity(intent);
        });

        // Share Mood
        shareAppButton.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Today my mood is...");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share Mood"));
        });
//        mood_disapButton.setOnClickListener(view -> {updateMood("disappointed");});
//        mood_sadImageButton.setOnClickListener(view -> {updateMood("sad");});
//        mood_neutralImageView.setOnClickListener(view-> {updateMood("normal");});
//        mood_happyImageView.setOnClickListener(view->{updateMood("happy");});
//        mood_shappyImageView.setOnClickListener(view->{updateMood("super happy");});
    }


    // Gesture Handling for Mood Changes
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getY() - e2.getY() > 50 && currentMoodIndex < 4) {
            currentMoodIndex++;
        } else if (e1.getY() - e2.getY() < 50 && currentMoodIndex > 0) {
            currentMoodIndex--;
        }
//        changeUiForMood(currentMoodIndex);
        SharedPreferencesHelper.saveMood(currentMoodIndex, currentDay, mPreferences);
        return true;
    }

    // Change Mood UI
    private void changeUiForMood(int moodIndex) {
        moodImageView.setImageResource(Constants.moodImagesArray[moodIndex]);
//        parentRelativeLayout.setBackgroundResource(Constants.moodColorsArray[moodIndex]);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, Constants.moodSoundsArray[moodIndex]);
        mediaPlayer.start();
    }
    private void updateMood(String mood) {
//        selectedMoodText.setText("You are feeling " + mood + "!");
//        parentRelativeLayout.setBackgroundResource(backgroundColor);
    }


    // Schedule Alarm for Daily Mood Tracking
    private void scheduleAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, UpdateDayReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    // Dummy Emotion Analysis (Replace with API)
    private void analyzeEmotion(String text) {
        // Replace with API call (e.g., MeaningCloud or DeepAI)
        resultText.setText("Emotion Detected: Happy 😊");
        resultText.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

//    private void startCamera() {
//        // Use ListenableFuture instead of CameraProviderFuture
//        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//
//        cameraProviderFuture.addListener(() -> {
//            try {
//                cameraProvider = cameraProviderFuture.get();
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//
//                // Build Preview
//                Preview preview = new Preview.Builder().build();
//                preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//                // Build ImageCapture
//                imageCapture = new ImageCapture.Builder()
//                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                        .build();
//
//                // Choose front camera
//                CameraSelector cameraSelector = new CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
//                        .build();
//
//                // Unbind use cases before rebinding
//                cameraProvider.unbindAll();
//
//                // Bind camera lifecycle to lifecycle owner
//                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
//
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
private void startCamera() {
    // Get an instance of the camera provider
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

    cameraProviderFuture.addListener(() -> {
        try {
            // Get the camera provider
            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

            // Create the preview object
            Preview preview = new Preview.Builder().build();

            // Initialize ImageCapture
            imageCapture = new ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build();

            // Use the front camera
            CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

            // Unbind all previous use cases
            cameraProvider.unbindAll();

            // Bind the preview and imageCapture use cases to the lifecycle
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            // Set the preview surface provider
            preview.setSurfaceProvider(previewView.getSurfaceProvider());

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }, ContextCompat.getMainExecutor(this));
}


    private void detectEmotion(File photoFile) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            // Fix rotation using EXIF
            ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationDegrees = 0;

            switch (rotation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationDegrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationDegrees = 270;
                    break;
            }

            // Rotate bitmap
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            // Show rotated image
            capturedImageView.setImageBitmap(rotatedBitmap);
            capturedImageView.setVisibility(View.VISIBLE);
            previewView.setVisibility(View.GONE);

            // Send rotated bitmap to ML Kit
            InputImage image = InputImage.fromBitmap(rotatedBitmap, 0);

            FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build();

            FaceDetector detector = FaceDetection.getClient(options);

            detector.process(image)
                    .addOnSuccessListener(faces -> {
                        if (faces.size() == 0) {
                            runOnUiThread(() -> {
                                resultText.setText("No face detected.");
                                resultText.setVisibility(View.VISIBLE);
//                                selectedMoodText.setText("Couldn't detect mood 😐");
                            });
                            return;
                        }

                        Face face = faces.get(0);

                        Float smileProb = face.getSmilingProbability();
                        Float leftEyeOpen = face.getLeftEyeOpenProbability();
                        Float rightEyeOpen = face.getRightEyeOpenProbability();

                        String emotion;

                        if (smileProb != null && leftEyeOpen != null && rightEyeOpen != null) {
                            if (smileProb > 0.6) {
                                emotion = "happy";
                            } else if (smileProb < 0.2 && leftEyeOpen > 0.6 && rightEyeOpen > 0.6) {
                                emotion = "sad";
                            } else if (smileProb < 0.2 && (leftEyeOpen < 0.4 || rightEyeOpen < 0.4)) {
                                emotion = "angry";
                            } else if (leftEyeOpen < 0.3 && rightEyeOpen < 0.3) {
                                emotion = "sleepy";
                            } else {
                                emotion = "neutral";
                            }
                        } else {
                            emotion = "neutral";
                        }

                        String moodResult = "Detected emotion: " + emotion;
                        runOnUiThread(() -> {

                            resultText.setText(moodResult);
                            resultText.setVisibility(View.VISIBLE);
                            btnRetake.setVisibility(View.VISIBLE);
                            btnCapture.setVisibility(View.GONE);
                            btnShowSongs.setVisibility(View.VISIBLE);
//                            updateMood(emotion);  // you can customize updateMood() per emotion
                            fetchSpotifySongs(emotion);  // Automatically fetch Spotify playlists based on detected emotion
                            new com.example.myapplication.repository.MoodLogger().logMood(emotion);
                        });

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            resultText.setText("Face detection failed!");
                            resultText.setVisibility(View.VISIBLE);
//                            selectedMoodText.setText("Couldn't detect mood 😐");
                        });
                    });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Image processing error", Toast.LENGTH_SHORT).show();
        }
    }
    private void restartCamera() {
        // Show camera preview again
        previewView.setVisibility(View.VISIBLE);
        capturedImageView.setVisibility(View.GONE);
        btnCapture.setVisibility(View.VISIBLE);

        btnRetake.setVisibility(View.GONE);
        resultText.setVisibility(View.GONE);
        btnShowSongs.setVisibility(View.GONE);
        btnShowPuzzles.setVisibility(View.GONE);

        // Start camera again
        startCamera();
    }


//    private void takePhoto() {
//        File photoFile = new File(getFilesDir(), "emotion.jpg");
//        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
//
//        imageCapture.takePicture(options, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
//            @Override
//            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                runOnUiThread(() -> {
//                    // Show image in ImageView
//                    Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//                    capturedImageView.setImageBitmap(bitmap);
//                    capturedImageView.setVisibility(View.VISIBLE);
//
//                    // Hide camera preview
//                    previewView.setVisibility(View.GONE);
//
//                    // Show Retake button
//                    btnRetake.setVisibility(View.VISIBLE);
//
//                    // Stop camera
//                    if (cameraProvider != null) {
//                        cameraProvider.unbindAll();
//                    }
//
//                    // Trigger Emotion Detection
//                    detectEmotion(photoFile);
//                });
//            }
//
//            @Override
//            public void onError(@NonNull ImageCaptureException exception) {
//                exception.printStackTrace();
//                Toast.makeText(MainActivity.this, "Capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
private void takePhoto() {
    // Check if imageCapture is null
    if (imageCapture == null) {
        Log.e("Camera", "ImageCapture is not initialized.");
        return;
    }

    // Create a file to save the captured photo
    File photoFile = new File(getExternalFilesDir(null), "photo_" + System.currentTimeMillis() + ".jpg");

    // Define output options for saving the captured photo
    ImageCapture.OutputFileOptions outputFileOptions =
            new ImageCapture.OutputFileOptions.Builder(photoFile).build();

    // Capture the photo
    imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    // Handle the successful capture of the photo
                    Log.d("Camera", "Photo saved to " + photoFile.getAbsolutePath());
                    detectEmotion(photoFile);
                    previewView.setVisibility(View.GONE);
                    btnCapture.setVisibility(View.GONE);
                    findViewById(R.id.horizontalActionButtons).setVisibility(View.VISIBLE);

                    // Show captured image and other UI
                    capturedImageView.setVisibility(View.VISIBLE);
                    btnRetake.setVisibility(View.VISIBLE);
                    resultText.setVisibility(View.VISIBLE);
                    btnShowSongs.setVisibility(View.VISIBLE);
                    btnShowPuzzles.setVisibility(View.VISIBLE);

                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    // Handle any errors during image capture
                    Log.e("Camera", "Error capturing photo: " + exception.getMessage());
                }
            }
    );
}

    private String getSpotifyAccessToken() throws IOException, JSONException {
        String clientId = "6d74ab83420d412bb194fe4884acb843";
        String clientSecret = "ecf8a95cdcf9429687ea5f63b9eea56e";

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(body)
                .addHeader("Authorization", "Basic " + encodedCredentials)
                .build();

        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String jsonData = response.body().string();
            JSONObject jsonObject = new JSONObject(jsonData);
            String accessToken = jsonObject.getString("access_token");
            Log.d("Spotify", "Access Token: " + accessToken); // Log the token
            return jsonObject.getString("access_token");  // Extract access token
        }

        return null;
    }


    private void fetchSpotifySongs(String emotion) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Get the Spotify access token
                    String accessToken = getSpotifyAccessToken();
                    if (accessToken == null) {
                        Log.e("Spotify", "Failed to get access token.");
                        return;
                    }

                    String url = "https://api.spotify.com/v1/search?q=" + emotion + "&type=playlist&limit=5";
                    Log.d("Spotify", "API URL: " + url); // Log the URL

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .addHeader("Authorization", "Bearer " + accessToken)
                            .build();

                    // Execute the request
                    Response response = client.newCall(request).execute();

                    // Log the response code and body
                    Log.d("Spotify", "Response code: " + response.code());  // Log status code
                    String responseBody = response.body() != null ? response.body().string() : "No response body";
                    Log.d("Spotify", "Response body: " + responseBody);  // Log response body

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);

                            // Log the JSON response structure for inspection
                            Log.d("Spotify", "JSON Response: " + jsonObject.toString());

                            // Extract the playlists
                            JSONArray items = jsonObject.getJSONObject("playlists").getJSONArray("items");

                            playlists = new ArrayList<>();
                            for (int i = 0; i < items.length(); i++) {
                                if (items.isNull(i)) {
                                    continue; // Skip null entries
                                }

                                // Parse the non-null item
                                JSONObject playlist = items.getJSONObject(i);
                                String playlistName = playlist.getString("name");
                                String playlistUri = playlist.getString("uri");
                                String playlistDescription = playlist.getString("description");
                                String playlistImageUrl = playlist.getJSONArray("images").getJSONObject(0).getString("url");

                                playlists.add(new Playlist(playlistName, playlistUri, playlistDescription, playlistImageUrl));
                            }

                            // Now, display the playlists
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Hide "Show Songs" button until emotion is detected
                                    btnShowSongs.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (JSONException e) {
                            Log.e("Spotify", "Error parsing JSON response: " + e.getMessage());
                        }
                    } else {
                        Log.e("Spotify", "Request failed: " + response.message());
                        Log.e("Spotify", "Error body: " + responseBody);  // Log the error body
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Spotify", "Error fetching songs: " + e.getMessage());
                }
            }
        }).start();
    }

    private void startPuzzleSession() {
        currentMood = resultText.getText().toString()
                .replace("Detected emotion: ", "").toLowerCase();

        currentPuzzleList = new ArrayList<>(PuzzleManager.getPuzzlesForMood(currentMood));
        Collections.shuffle(currentPuzzleList);
        currentPuzzleIndex = 0;

        showCurrentPuzzle();
    }

    private void showCurrentPuzzle() {
        if (currentPuzzleIndex >= currentPuzzleList.size()) {
            // All puzzles shown, reshuffle and start over
            Collections.shuffle(currentPuzzleList);
            currentPuzzleIndex = 0;
        }

        Puzzle currentPuzzle = currentPuzzleList.get(currentPuzzleIndex);
        showPuzzleDialog(currentPuzzle);
    }

    private void showPuzzleDialog(Puzzle puzzle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(puzzle.getTitle());
        builder.setMessage(puzzle.getQuestion());

        final EditText answerInput = new EditText(this);
        answerInput.setHint("Type your answer...");
        builder.setView(answerInput);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String userAnswer = answerInput.getText().toString().trim();
            if (userAnswer.isEmpty()) {
                Toast.makeText(this, "Please enter an answer", Toast.LENGTH_SHORT).show();
                return;
            }
            checkAnswer(userAnswer, puzzle);
        });

        builder.setNegativeButton("Skip", (dialog, which) -> {
            moveToNextPuzzle();
        });

        builder.show();
    }

    private void checkAnswer(String userAnswer, Puzzle puzzle) {
        boolean isCorrect = userAnswer.equalsIgnoreCase(puzzle.getAnswer());

        AlertDialog.Builder resultBuilder = new AlertDialog.Builder(this);
        resultBuilder.setTitle(isCorrect ? "Correct! 🎉" : "Incorrect ❌");

        if (isCorrect) {
            resultBuilder.setMessage("Great job!");
        } else {
            resultBuilder.setMessage("The correct answer was: " + puzzle.getAnswer());
        }

        resultBuilder.setPositiveButton("Next Puzzle", (dialog, which) -> {
            moveToNextPuzzle();
        });

        resultBuilder.setNegativeButton("Finish", null);
        resultBuilder.show();
    }

    private void moveToNextPuzzle() {
        currentPuzzleIndex++;
        showCurrentPuzzle();
    }







//    private void displayPlaylists(List<Playlist> playlists) {
//        RecyclerView recyclerView = findViewById(R.id.recycler_view);
//        PlaylistAdapter adapter = new PlaylistAdapter(playlists);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }







    @Override public boolean onDown(MotionEvent e) { return false; }
    @Override public void onShowPress(MotionEvent e) { }
    @Override public boolean onSingleTapUp(MotionEvent e) { return false; }
    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
    @Override public void onLongPress(MotionEvent e) { }

}