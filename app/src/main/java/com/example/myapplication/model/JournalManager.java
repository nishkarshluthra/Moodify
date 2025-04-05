package com.example.myapplication.model;



import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;

public class JournalManager {
    private static final String JOURNAL_PREFS = "journal_prefs";
    private static final String ENTRIES_KEY = "journal_entries";
    private static final String ENTRY_PREFIX = "journal_entry_";

    public static class JournalEntry {
        private String date;
        private String text;
        private Bitmap photo;

        public JournalEntry(String date, String text, Bitmap photo) {
            this.date = date;
            this.text = text;
            this.photo = photo;
        }

        // Getters
        public String getDate() { return date; }
        public String getText() { return text; }
        public Bitmap getPhoto() { return photo; }
    }

    public static void saveJournalEntry(Context context, String date, String text, Bitmap photo) {
        SharedPreferences prefs = context.getSharedPreferences(JOURNAL_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save text
        editor.putString(ENTRY_PREFIX + date + "_TEXT", text);

        // Save photo
        if (photo != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            String encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
            editor.putString(ENTRY_PREFIX + date + "_PHOTO", encodedImage);
        } else {
            editor.remove(ENTRY_PREFIX + date + "_PHOTO");
        }

        // Update entry dates
        Set<String> dates = prefs.getStringSet(ENTRIES_KEY, new HashSet<>());
        Set<String> newDates = new HashSet<>(dates);
        newDates.add(date);
        editor.putStringSet(ENTRIES_KEY, newDates);

        editor.apply();
    }

    public static JournalEntry getJournalEntry(Context context, String date) {
        SharedPreferences prefs = context.getSharedPreferences(JOURNAL_PREFS, Context.MODE_PRIVATE);
        String text = prefs.getString(ENTRY_PREFIX + date + "_TEXT", null);
        if (text == null) return null;

        String encodedImage = prefs.getString(ENTRY_PREFIX + date + "_PHOTO", null);
        Bitmap photo = null;
        if (encodedImage != null) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            photo = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }

        return new JournalEntry(date, text, photo);
    }

    public static Set<String> getAllEntryDates(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(JOURNAL_PREFS, Context.MODE_PRIVATE);
        return prefs.getStringSet(ENTRIES_KEY, new HashSet<>());
    }
}