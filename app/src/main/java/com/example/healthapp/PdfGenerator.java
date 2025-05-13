package com.example.healthapp;

import android.content.Context;
import android.os.Environment;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment; // Fixed import
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfGenerator {

    public static File generateHealthMetricsPdf(Context context, String steps, String bpm, String sleepHours) {
        // Create time-stamped filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "HealthMetrics_" + timeStamp + ".pdf";

        // Get downloads directory
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(downloadsDir, fileName);

        try {
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add title
            document.add(new Paragraph("Health Metrics Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20));

            document.add(new Paragraph("\n"));

            // Add date
            String currentDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());
            document.add(new Paragraph("Generated on: " + currentDate)
                    .setTextAlignment(TextAlignment.LEFT));

            document.add(new Paragraph("\n"));

            // Create table for metrics
            float[] columnWidths = {150f, 150f};
            Table table = new Table(columnWidths);

            // Add table headers
            table.addCell(new Paragraph("Metric").setBold());
            table.addCell(new Paragraph("Value").setBold());

            // Add health data
            table.addCell(new Paragraph("Steps"));
            table.addCell(new Paragraph(steps));

            table.addCell(new Paragraph("Heart Rate (BPM)"));
            table.addCell(new Paragraph(bpm));

            table.addCell(new Paragraph("Sleep Hours"));
            table.addCell(new Paragraph(sleepHours));

            document.add(table);
            document.close();

            return pdfFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}