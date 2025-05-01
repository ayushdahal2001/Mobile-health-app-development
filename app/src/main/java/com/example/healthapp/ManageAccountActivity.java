package com.example.healthapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ManageAccountActivity extends BaseActivity {

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private Button btnChangePassword;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private View progressBar, passwordForm;
    private ImageView ivExpandPassword;
    private boolean isPasswordFormExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize views
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        progressBar = findViewById(R.id.progressBar);
        passwordForm = findViewById(R.id.passwordForm);
        ivExpandPassword = findViewById(R.id.ivExpandPassword);

        // Set up expand/collapse functionality
        View passwordHeader = findViewById(R.id.passwordHeader);
        passwordHeader.setOnClickListener(v -> togglePasswordForm());

        btnChangePassword.setOnClickListener(v -> changePassword());
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