package com.example.appdevadmin;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

    public void runBillingCheck() {
        Calendar today = Calendar.getInstance();
        int currentDay = today.get(Calendar.DAY_OF_MONTH);
        int currentMonth = today.get(Calendar.MONTH);
        int currentYear = today.get(Calendar.YEAR);
        String currentMonthName = MONTH_NAMES[currentMonth];

        // REQUIREMENT: Only process users who are Active.
        // Inactive accounts are completely skipped here.
        db.collection("users")
                .whereEqualTo("role", "Tenant")
                .whereEqualTo("status", "Active")
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String uid = doc.getId();
                        String fullName = doc.getString("fullName");
                        String roomNumber = doc.getString("roomNumber");
                        String roomId = doc.getString("roomId");
                        String leaseStart = doc.getString("leaseStart");
                        double monthlyRent = doc.getDouble("monthlyRent") != null ? doc.getDouble("monthlyRent") : 0;
                        int rentDueDate = doc.getLong("rentDueDate") != null ? doc.getLong("rentDueDate").intValue() : 1;

                        checkAndCreatePayment(uid, fullName, roomNumber, roomId, leaseStart,
                                monthlyRent, rentDueDate, currentDay, currentMonthName, currentMonth, currentYear);
                    }
                });
    }

    private void checkAndCreatePayment(String uid, String fullName, String roomNumber,
                                       String roomId, String leaseStart, double monthlyRent, int rentDueDate,
                                       int currentDay, String currentMonthName,
                                       int currentMonth, int currentYear) {

        db.collection("payments")
                .whereEqualTo("userId", uid)
                .whereEqualTo("month", currentMonthName)
                .whereEqualTo("year", currentYear)
                .get()
                .addOnSuccessListener(existing -> {
                    if (!existing.isEmpty()) {
                        // Payment already exists
                        for (QueryDocumentSnapshot payDoc : existing) {
                            String status = payDoc.getString("status");
                            // Only update Pending payments to Overdue
                            if ("Pending".equals(status)) {
                                if (currentDay > rentDueDate && isEligibleForOverdue(leaseStart, rentDueDate, currentMonth, currentYear)) {
                                    updateStatusToOverdue(payDoc.getId(), fullName, roomNumber, monthlyRent);
                                }
                            }
                        }
                    } else {
                        // No record yet - Create one only if lease has started
                        if (hasLeaseStarted(leaseStart, currentMonth, currentYear)) {
                            int daysUntilDue = rentDueDate - currentDay;

                            // Generate bill 7 days before due date or if already overdue
                            if (daysUntilDue <= 7) {
                                String status;
                                if (currentDay > rentDueDate && isEligibleForOverdue(leaseStart, rentDueDate, currentMonth, currentYear)) {
                                    status = "Overdue";
                                    createPaymentAndLog(uid, fullName, roomNumber, roomId, monthlyRent, rentDueDate, currentMonthName, currentYear, status);
                                } else {
                                    status = "Pending";
                                    createPaymentRecord(uid, fullName, roomNumber, roomId, monthlyRent, rentDueDate, currentMonthName, currentYear, status);
                                }
                            }
                        }
                    }
                });
    }

    private boolean isEligibleForOverdue(String leaseStartStr, int rentDueDate, int currentMonth, int currentYear) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date leaseDate = sdf.parse(leaseStartStr);
            Calendar leaseCal = Calendar.getInstance();
            leaseCal.setTime(leaseDate);

            if (currentYear > leaseCal.get(Calendar.YEAR)) return true;
            if (currentYear == leaseCal.get(Calendar.YEAR) && currentMonth > leaseCal.get(Calendar.MONTH)) return true;

            // If same month/year, only overdue if they moved in on or before the due date
            return leaseCal.get(Calendar.DAY_OF_MONTH) <= rentDueDate;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean hasLeaseStarted(String leaseStartStr, int currentMonth, int currentYear) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date leaseDate = sdf.parse(leaseStartStr);
            Calendar leaseCal = Calendar.getInstance();
            leaseCal.setTime(leaseDate);

            if (currentYear > leaseCal.get(Calendar.YEAR)) return true;
            return currentYear == leaseCal.get(Calendar.YEAR) && currentMonth >= leaseCal.get(Calendar.MONTH);
        } catch (Exception e) {
            return true;
        }
    }

    private void updateStatusToOverdue(String paymentId, String fullName, String roomNumber, double amount) {
        WriteBatch batch = db.batch();
        batch.update(db.collection("payments").document(paymentId), "status", "Overdue");

        Map<String, Object> log = new HashMap<>();
        log.put("title", "Payment Overdue: " + roomNumber + " " + fullName);
        log.put("details", String.format("₱%,.2f - Monthly Rent", amount));
        log.put("timestamp", Timestamp.now());
        log.put("type", "payment");

        batch.set(db.collection("activity_logs").document(), log);
        batch.commit();
    }

    private void createPaymentAndLog(String uid, String fullName, String roomNumber, String roomId, double amount, int due, String month, int year, String status) {
        createPaymentRecord(uid, fullName, roomNumber, roomId, amount, due, month, year, status);

        Map<String, Object> log = new HashMap<>();
        log.put("title", "Payment Overdue: " + roomNumber + " " + fullName);
        log.put("details", String.format("₱%,.2f - Monthly Rent", amount));
        log.put("timestamp", Timestamp.now());
        log.put("type", "payment");
        db.collection("activity_logs").add(log);
    }

    private void createPaymentRecord(String uid, String fullName, String roomNumber, String roomId, double monthlyRent, int rentDueDate, String month, int year, String status) {
        Map<String, Object> payment = new HashMap<>();
        payment.put("userId", uid);
        payment.put("tenantName", fullName);
        payment.put("roomNumber", roomNumber);
        payment.put("roomId", roomId);
        payment.put("amount", monthlyRent);
        payment.put("month", month);
        payment.put("year", year);
        payment.put("dueDate", rentDueDate);
        payment.put("status", status);
        payment.put("type", "Monthly Rent");
        payment.put("createdAt", Timestamp.now());

        db.collection("payments").add(payment);
    }
}