# Moodify - README

## Overview

Moodify is a mobile application designed to assist users in monitoring and improving their emotional well-being. It utilizes AI-powered sentiment analysis and facial expression detection to track moods and provide personalized recommendations, motivational notifications, and professional support.

## Features

### Customer Features


- *Mood Tracking*: Log emotions through text, emojis, and AI-based facial expression detection.

- *Personalized Recommendations*: Suggestions for mental health activities, music, meditation, and self-care routines.

- *Motivational Support*: Timely notifications and reminders to encourage positive mental health habits.

- *Professional Support*: Access to licensed mental health professionals via chat and video sessions.

- *Journaling & Self-Expression*: Tools for writing, voice notes, and creative outlets.

- *Mood Insights & Analytics*: Data visualizations and trend analysis for emotional patterns.

## Tech Stack

- *Frontend*
    - Java (for Android development)
    - XML (for UI design)

- *Backend*
    - Firebase / Firestore (for real-time database, and cloud storage)

- *Authentication*
     - Firebase Authentication

## Installation

### Prerequisites

Ensure you have the following installed:


- *Android Studio*: Install from [Android Developer website](https://developer.android.com/studio).
- *Java Development Kit (JDK)*: Install from [Oracle-JDK] (https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)

- *Firebase CLI*: Install via npm:
  sh
  npm install -g firebase-tools
  

### Setup Instructions

1. *Extract the provided ZIP file* to a suitable directory on your system.
2. *Open the project in Android Studio or Visual Studio Code*:
   - In Android Studio: Open the extracted folder.
   - In VS Code: Navigate to the folder and run code . in the terminal.
   
3. *Set up Firebase*:
   - Go to [Firebase Console](https://console.firebase.google.com/) and create a new project.
   - Add an Android app to the project and download google-services.json.
   - Place google-services.json inside android/app/.
4. *Enable Firebase Authentication*:
   - In the Firebase Console, go to *Authentication → Sign-in method*.
   - Enable *Email/Password Authentication*.
5. *Enable Firebase Firestore*:
   - In Firebase Console, navigate to *Firestore Database* and create a new database.
   - Set Firestore rules as needed.
6. *Run the application*:
   - Click Run in Android Studio
   - If running on a physical device, enable *USB Debugging*.
   - For emulator usage, launch an emulator from Android Studio.


