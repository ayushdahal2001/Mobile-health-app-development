package com.example.healthapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private String currentUserId;
    private long longPressStartTime;

    // Permission request codes
    private static final int REQUEST_CALL_PHONE = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private static final int REQUEST_MEDIASTORE_WRITE = 3;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (auth.getCurrentUser() != null) {
            currentUserId = auth.getCurrentUser().getUid();
        }

        // Initialize buttons
        Button btnSyncDevice = findViewById(R.id.btnSyncDevice);
        Button btnExportData = findViewById(R.id.btnExportData);
        Button btnManageAccount = findViewById(R.id.btnManageAccount);
        Button btnHealthMetrics = findViewById(R.id.btnHealthMetrics);
        Button btnHome = findViewById(R.id.btnHome);
        Button btnNotification = findViewById(R.id.btnNotification);
        Button btnSettings = findViewById(R.id.btnSettings);

        // Emergency SOS long press
        View emergencySosArea = findViewById(R.id.emergencySosArea);
        emergencySosArea.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    longPressStartTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_UP:
                    if (System.currentTimeMillis() - longPressStartTime >= 1000) {
                        callEmergencyContact();
                    }
                    break;
            }
            return true;
        });

        // Set click listeners
        btnHealthMetrics.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HealthMetricsActivity.class)));
        btnNotification.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NotificationsActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        btnManageAccount.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ManageAccountActivity.class)));
        btnExportData.setOnClickListener(v -> exportHealthData());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    // Emergency call functionality
    private void callEmergencyContact() {
        String emergencyNumber = getEmergencyContactNumber();
        vibratePhone();

        new AlertDialog.Builder(this)
                .setTitle("Emergency Call")
                .setMessage("Call " + emergencyNumber + "?")
                .setPositiveButton("Call", (dialog, which) -> makeEmergencyCall(emergencyNumber))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void makeEmergencyCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PHONE
            );
        }
    }

    private String getEmergencyContactNumber() {
        SharedPreferences prefs = getSharedPreferences("HealthAppPrefs", MODE_PRIVATE);
        String savedContact = prefs.getString("emergency_contact", "");
        return TextUtils.isEmpty(savedContact) ? "911" : savedContact;
    }

    // PDF Export functionality
    private void exportHealthData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestMediaStoreWriteAccess();
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                createHealthMetricsPdf();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestMediaStoreWriteAccess() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "HealthMetrics_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf");
        startActivityForResult(intent, REQUEST_MEDIASTORE_WRITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MEDIASTORE_WRITE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                generatePdfToUri(data.getData());
            }
        }
    }

    private void generatePdfToUri(Uri uri) {


        mDatabase.child("users").child(currentUserId).child("healthMetrics")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try (OutputStream os = getContentResolver().openOutputStream(uri);
                             PdfWriter writer = new PdfWriter(os);
                             PdfDocument pdf = new PdfDocument(writer);
                             Document document = new Document(pdf)) {

                            // Add metadata (helps with file recognition)
                            pdf.getDocumentInfo()
                                    .setTitle("Health Metrics Report")
                                    .setAuthor("Health App");

                            // Add content
                            addPdfContent(document, snapshot);

                            Toast.makeText(MainActivity.this,
                                    "Report generated successfully", Toast.LENGTH_LONG).show();

                            // Explicitly open the PDF after creation
                            openPdfFile(uri);
                        } catch (IOException e) {
                            Log.e("PDF", "Error generating PDF", e);
                            Toast.makeText(MainActivity.this,
                                    "Failed to generate report", Toast.LENGTH_SHORT).show();
                        } finally {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                      
                        Toast.makeText(MainActivity.this,
                                "Failed to load health data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPdfContent(Document document, DataSnapshot snapshot) throws IOException {
        // Add title
        Paragraph title = new Paragraph("Health Metrics Report")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20);
        document.add(title);

        // Add date
        String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        document.add(new Paragraph("Generated on: " + currentDate)
                .setMarginTop(10f));

        // Add empty line
        document.add(new Paragraph("\n"));

        // Get health data
        String steps = snapshot.child("steps").exists() ?
                snapshot.child("steps").getValue(String.class) : "0";
        String bpm = snapshot.child("bpm").exists() ?
                snapshot.child("bpm").getValue(String.class) : "N/A";
        String sleepHours = snapshot.child("sleepHours").exists() ?
                snapshot.child("sleepHours").getValue(String.class) : "0";

        // Create table
        float[] columnWidths = {200f, 200f};
        Table table = new Table(columnWidths);

        // Add table headers
        table.addCell(createCell("Metric", true));
        table.addCell(createCell("Value", true));

        // Add data rows
        table.addCell(createCell("Steps", false));
        table.addCell(createCell(steps, false));

        table.addCell(createCell("Heart Rate (BPM)", false));
        table.addCell(createCell(bpm, false));

        table.addCell(createCell("Sleep Hours", false));
        table.addCell(createCell(sleepHours, false));

        document.add(table);
    }

    private Paragraph createCell(String text, boolean isHeader) {
        Paragraph p = new Paragraph(text);
        if (isHeader) {
            p.setBold();
        }
        return p;
    }

    private void createHealthMetricsPdf() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String fileName = "HealthMetrics_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".pdf";
        File pdfFile = new File(downloadsDir, fileName);



        mDatabase.child("users").child(currentUserId).child("healthMetrics")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try (PdfWriter writer = new PdfWriter(pdfFile);
                             PdfDocument pdf = new PdfDocument(writer);
                             Document document = new Document(pdf)) {

                            pdf.getDocumentInfo()
                                    .setTitle("Health Metrics Report")
                                    .setAuthor("Health App");

                            addPdfContent(document, snapshot);

                            Toast.makeText(MainActivity.this,
                                    "Report saved to Downloads", Toast.LENGTH_LONG).show();

                            // Open the file after creation
                            Uri uri = Uri.fromFile(pdfFile);
                            openPdfFile(uri);
                        } catch (IOException e) {
                            Toast.makeText(MainActivity.this,
                                    "Failed to save report", Toast.LENGTH_SHORT).show();
                            Log.e("PDF", "Error writing PDF", e);
                        } finally {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(MainActivity.this,
                                "Failed to load health data", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void openPdfFile(Uri pdfUri) {
        try {
            // Create view intent
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Verify there's a PDF viewer installed
            PackageManager pm = getPackageManager();
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent);
            } else {
                // If no PDF viewer, suggest installing one
                new AlertDialog.Builder(this)
                        .setTitle("No PDF Viewer")
                        .setMessage("Would you like to install a PDF viewer app?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=com.adobe.reader")));
                            } catch (android.content.ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=com.adobe.reader")));
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error opening PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("PDF", "Error opening file", e);
        }
    }

    // Permission handling
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 0) return;

        switch (requestCode) {
            case REQUEST_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callEmergencyContact();
                } else {
                    Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_STORAGE_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createHealthMetricsPdf();
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void vibratePhone() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
    }
}