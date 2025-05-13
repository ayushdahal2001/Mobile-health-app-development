package com.example.healthapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManageAccountActivity extends BaseActivity {

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Button btnChangePassword;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private View progressBar, passwordForm;
    private ImageView ivExpandPassword;
    private boolean isPasswordFormExpanded = false;
    private EditText etEmergencyContact;
    private Button btnSaveEmergencyContact;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        currentUserId = user != null ? user.getUid() : null;

        if (currentUserId == null) {
            Toast.makeText(this, "User not signed in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        passwordForm = findViewById(R.id.passwordForm);
        ivExpandPassword = findViewById(R.id.ivExpandPassword);
        etEmergencyContact = findViewById(R.id.etEmergencyContact);
        btnSaveEmergencyContact = findViewById(R.id.btnSaveEmergencyContact);

        // Load emergency contact
        loadEmergencyContact();

        // Set up listeners
        findViewById(R.id.passwordHeader).setOnClickListener(v -> togglePasswordForm());
        btnChangePassword.setOnClickListener(v -> changePassword());
        btnSaveEmergencyContact.setOnClickListener(v -> saveEmergencyContact());
    }

    private void loadEmergencyContact() {
        // First try to load from local storage
        SharedPreferences prefs = getSharedPreferences("HealthAppPrefs", MODE_PRIVATE);
        String savedContact = prefs.getString("emergency_contact", "");
        etEmergencyContact.setText(savedContact);

        // Then sync from Firebase
        mDatabase.child("users").child(currentUserId).child("emergencyContact")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String firebaseContact = snapshot.getValue(String.class);
                        if (firebaseContact != null && !firebaseContact.isEmpty()) {
                            etEmergencyContact.setText(firebaseContact);
                            // Update local storage with Firebase value
                            SharedPreferences prefs = getSharedPreferences("HealthAppPrefs", MODE_PRIVATE);
                            prefs.edit().putString("emergency_contact", firebaseContact).apply();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w("ManageAccount", "Failed to load emergency contact", error.toException());
                    }
                });
    }

    private void saveEmergencyContact() {
        String contactNumber = etEmergencyContact.getText().toString().trim();

        if (TextUtils.isEmpty(contactNumber)) {
            // If empty, we'll default to 911 when calling
            contactNumber = ""; // Clear any existing number
        } else if (!contactNumber.matches("^[0-9]{3,}$")) {
            // Basic validation - at least 3 digits
            etEmergencyContact.setError("Enter a valid phone number");
            return;
        }

        // Save locally
        SharedPreferences prefs = getSharedPreferences("HealthAppPrefs", MODE_PRIVATE);
        prefs.edit().putString("emergency_contact", contactNumber).apply();

        // Save to Firebase if user is logged in
        if (currentUserId != null) {
            mDatabase.child("users").child(currentUserId).child("emergencyContact")
                    .setValue(contactNumber)
                    .addOnCompleteListener(task -> {
                        String message = task.isSuccessful() ?
                                "Emergency contact saved" :
                                "Saved locally but cloud sync failed";
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Emergency contact saved locally", Toast.LENGTH_SHORT).show();
        }
    }

    private void togglePasswordForm() {
        if (isPasswordFormExpanded) {
            // Collapse the form
            passwordForm.setVisibility(View.GONE);
            ivExpandPassword.setImageResource(R.drawable.ic_expand_more);
        } else {
            // Expand the form
            passwordForm.setVisibility(View.VISIBLE);
            ivExpandPassword.setImageResource(R.drawable.ic_expand_less);
        }
        isPasswordFormExpanded = !isPasswordFormExpanded;
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Enter your current password");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Enter new password");
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password should be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            etConfirmNewPassword.setError("Password doesn't match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Reauthenticate user before changing password
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update password
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(updateTask -> {
                                    progressBar.setVisibility(View.GONE);

                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(ManageAccountActivity.this,
                                                "Password changed successfully", Toast.LENGTH_SHORT).show();
                                        // Clear fields and collapse form
                                        etCurrentPassword.setText("");
                                        etNewPassword.setText("");
                                        etConfirmNewPassword.setText("");
                                        togglePasswordForm();
                                    } else {
                                        Toast.makeText(ManageAccountActivity.this,
                                                "Failed to change password: " + updateTask.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ManageAccountActivity.this,
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });



    }
}