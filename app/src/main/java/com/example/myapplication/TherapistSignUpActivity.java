package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TherapistSignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, loginName,Specialization;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist_signup);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        loginName = findViewById(R.id.login_name);
        Specialization=findViewById(R.id.Specialization);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String name = loginName.getText().toString().trim();
                String specialization = Specialization.getText().toString().trim();
                if (user.isEmpty()) {
                    signupEmail.setError("Email cannot be empty");
                    return;
                }
                if (pass.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                    return;
                }
                if (name.isEmpty()) {
                    loginName.setError("Name cannot be empty");
                    return;
                }
                if(specialization.isEmpty()){
                    Specialization.setError("Specialization cannot be empty");
                }

                auth.createUserWithEmailAndPassword(user, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = auth.getCurrentUser();
                                    if (firebaseUser != null) {
                                        saveUserDataToFirestore(firebaseUser.getUid(), user, name,specialization);
                                    }
                                    Toast.makeText(TherapistSignUpActivity.this, "SignUp Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(TherapistSignUpActivity.this, TherapistLoginActivity.class));
                                } else {
                                    Toast.makeText(TherapistSignUpActivity.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TherapistSignUpActivity.this, TherapistLoginActivity.class));
            }
        });
    }

    private void saveUserDataToFirestore(String uid, String email, String name, String specialization) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDoc = db.collection("therapists").document(uid);

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("email", email);
        userData.put("name", name);
        userData.put("specialization", specialization);
        userData.put("available",true);

        userDoc.set(userData)
                .addOnSuccessListener(aVoid -> Toast.makeText(TherapistSignUpActivity.this, "User data saved", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(TherapistSignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show());
    }
}
