package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.JournalManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JournalEntryActivity extends AppCompatActivity {
    private EditText etJournalEntry;
    private ImageView ivPhotoPreview;
    private String selectedDate;
    private Bitmap selectedPhoto;
    private Button btnSave, btnEdit, btnAddPhoto;

    private final ActivityResultLauncher<Intent> photoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        Uri imageUri = result.getData().getData();
                        selectedPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        ivPhotoPreview.setImageBitmap(selectedPhoto);
                        ivPhotoPreview.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journal_entry);

        etJournalEntry = findViewById(R.id.etJournalEntry);
        ivPhotoPreview = findViewById(R.id.ivPhotoPreview);
        btnSave = findViewById(R.id.btnSaveJournal);
        btnEdit = findViewById(R.id.btnEdit);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        TextView tvDate = findViewById(R.id.tvDate);

        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        if (selectedDate == null) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
        tvDate.setText(selectedDate);

        // Load existing entry if available
        JournalManager.JournalEntry existingEntry = JournalManager.getJournalEntry(this, selectedDate);
        if (existingEntry != null) {
            etJournalEntry.setText(existingEntry.getText());
            if (existingEntry.getPhoto() != null) {
                selectedPhoto = existingEntry.getPhoto();
                ivPhotoPreview.setImageBitmap(selectedPhoto);
                ivPhotoPreview.setVisibility(View.VISIBLE);
            }
            setupUIForExistingEntry();
        } else {
            setupUIForNewEntry();
        }

        btnAddPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            photoPickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> saveEntry());
        btnEdit.setOnClickListener(v -> enableEditing());
    }

    private void setupUIForNewEntry() {
        etJournalEntry.setEnabled(true);
        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
    }

    private void setupUIForExistingEntry() {
        etJournalEntry.setEnabled(false);
        btnEdit.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
    }

    private void enableEditing() {
        etJournalEntry.setEnabled(true);
        btnEdit.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
    }

    private void saveEntry() {
        String entryText = etJournalEntry.getText().toString().trim();
        if (entryText.isEmpty()) {
            Toast.makeText(this, "Journal entry cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        JournalManager.saveJournalEntry(this, selectedDate, entryText, selectedPhoto);
        Toast.makeText(this, "Entry saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}