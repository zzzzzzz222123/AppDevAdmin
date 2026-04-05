package com.example.appdevadmin;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BillingManager {

    private final FirebaseFirestore db;

    private static final String[] MONTH_NAMES = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    public BillingManager() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Call this when the app loads.
     * Checks all tenants and generates/updates payment records for the current month.
     */
    public void runBillingCheck() {
        Calendar today = Calendar.getInstance();
        int currentDay = today.get(Calendar.DAY_OF_MONTH);
        int currentMonth = today.get(Calendar.MONTH);       // 0-based
        int currentYear = today.get(Calendar.YEAR);
        String currentMonthName = MONTH_NAMES[currentMonth];

        db.collection("users")
                .whereEqualTo("role", "Tenant")
                .whereEqualTo("status", "Active")
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String uid = doc.getId();
                        String fullName = doc.getString("fullName") != null ? doc.getString("fullName") : "";
                        String roomNumber = doc.getString("roomNumber") != null ? doc.getString("roomNumber") : "";
                        String roomId = doc.getString("roomId") != null ? doc.getString("roomId") : "";
                        double monthlyRent = doc.getDouble("monthlyRent") != null ? doc.getDouble("monthlyRent") : 0;
                        int rentDueDate = doc.getLong("rentDueDate") != null ? doc.getLong("rentDueDate").intValue() : 1;

                        // Check if a payment record already exists for this month
                        checkAndCreatePayment(uid, fullName, roomNumber, roomId,
                                monthlyRent, rentDueDate,
                                currentDay, currentMonthName, currentMonth, currentYear);
                    }
                });
    }

    private void checkAndCreatePayment(String uid, String fullName, String roomNumber,
                                       String roomId, double monthlyRent, int rentDueDate,
                                       int currentDay, String currentMonthName,
                                       int currentMonth, int currentYear) {

        db.collection("payments")
                .whereEqualTo("userId", uid)
                .whereEqualTo("month", currentMonthName)
                .whereEqualTo("year", currentYear)
                .get()
                .addOnSuccessListener(existing -> {
                    if (!existing.isEmpty()) {
                        // Payment record exists — check if it needs to be updated to Overdue
                        for (QueryDocumentSnapshot payDoc : existing) {
                            String status = payDoc.getString("status");
                            if (status != null && !status.equals("Paid")) {
                                updateStatusIfNeeded(payDoc.getId(), currentDay, rentDueDate);
                            }
                        }
                    } else {
                        // No record yet — create one if within billing window
                        int daysUntilDue = rentDueDate - currentDay;

                        // Generate bill 7 days before due date
                        if (daysUntilDue <= 7) {
                            String status;
                            if (currentDay > rentDueDate) {
                                status = "Overdue";
                            } else {
                                status = "Pending";
                            }
                            createPaymentRecord(uid, fullName, roomNumber, roomId,
                                    monthlyRent, rentDueDate, currentMonthName,
                                    currentYear, status);
                        }
                    }
                });
    }

    private void updateStatusIfNeeded(String paymentId, int currentDay, int rentDueDate) {
        if (currentDay > rentDueDate) {
            db.collection("payments").document(paymentId)
                    .update("status", "Overdue");
        }
    }

    private void createPaymentRecord(String uid, String fullName, String roomNumber,
                                     String roomId, double monthlyRent, int rentDueDate,
                                     String month, int year, String status) {
        Map<String, Object> payment = new HashMap<>();
        payment.put("userId", uid);
        payment.put("tenantName", fullName);
        payment.put("roomNumber", roomNumber);
        payment.put("roomId", roomId);
        payment.put("amount", monthlyRent);
        payment.put("month", month);
        payment.put("year", year);
        payment.put("dueDate", rentDueDate);
        payment.put("paymentDate", null);
        payment.put("method", null);
        payment.put("notes", null);
        payment.put("status", status);
        payment.put("type", "Monthly Rent"); // ADDED THIS LINE
        payment.put("paidAt", null);
        payment.put("createdAt", Timestamp.now());


        db.collection("payments").add(payment);
    }
}