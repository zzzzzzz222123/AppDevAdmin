package com.example.appdevadmin;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.List;

/**
 * Mirrors the same Firestore document the tenant app writes.
 */
public class AdminMaintenanceRequest {

    @DocumentId
    public String id;

    public String tenantId;
    public String title;
    public String description;
    public String unit;
    public String category;
    public String priority;
    public String status;       // "pending" | "ongoing" | "completed"
    public String adminNotes;
    public List<String> photoUrls;
    public Timestamp submittedAt;
    public Timestamp updatedAt;

    public AdminMaintenanceRequest() {}

    public String formattedDate() {
        if (submittedAt == null) return "—";
        java.text.SimpleDateFormat sdf =
            new java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault());
        return "Submitted: " + sdf.format(submittedAt.toDate());
    }

    public int statusColor() {
        if (status == null) return 0xFFEF6C00;
        switch (status) {
            case "ongoing":   return 0xFF1976D2;
            case "completed": return 0xFF2E7D32;
            default:          return 0xFFEF6C00;
        }
    }

    public int sidebarColor() {
        if (status == null) return 0xFFEF6C00;
        switch (status) {
            case "ongoing":   return 0xFF1976D2;
            case "completed": return 0xFF2E7D32;
            default:          return 0xFFEF6C00;
        }
    }
}
