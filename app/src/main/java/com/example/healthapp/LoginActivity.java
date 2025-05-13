package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private TextView signupRedirectText;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Check if user is already logged in
        if (auth.getCurrentUser() != null) {
            redirectToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        signupRedirectText = findViewById(R.id.signupRedirectText);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> loginUser());
        signupRedirectText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        onLoginSuccess();
                    } else {
                        onLoginFailure(task.getException().getMessage());
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }
        return true;
    }

    private void onLoginSuccess() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            storeUserData(user); // Save user data to Firestore
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            redirectToMainActivity();
        }
    }

    private void storeUserData(FirebaseUser user) {
        if (user == null || user.getEmail() == null) return;

        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create user data map
        Map<String, Object> userData = new HashMap<>();
        userData.put("UserEmail", user.getEmail());
        userData.put("createdAt", FieldValue.serverTimestamp());

        // Add document to "users" collection
        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d("LoginActivity", "User data saved"))
                .addOnFailureListener(e -> Log.e("LoginActivity", "Error saving user data", e));
    }

    private void redirectToMainActivity() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void onLoginFailure(String errorMessage) {
        Toast.makeText(this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
        Log.e("LoginActivity", "Login failed: " + errorMessage);
    }
}