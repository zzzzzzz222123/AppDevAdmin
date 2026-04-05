package com.example.appdevadmin;

public class PaymentModel {
    private String id;
    private String userId;
    private String tenantName;
    private String roomNumber;
    private double amount;
    private String month;
    private int year;
    private int dueDate;
    private String paymentDate;
    private String method;
    private String notes;
    private String status;
    private String type; // Added for Monthly Rent / Invoice identification

    // Empty constructor for Firebase
    public PaymentModel() {}

    // Full constructor
    public PaymentModel(String id, String userId, String tenantName, String roomNumber,
                        double amount, String month, int year, int dueDate,
                        String paymentDate, String method, String notes, String status, String type) {
        this.id = id;
        this.userId = userId;
        this.tenantName = tenantName;
        this.roomNumber = roomNumber;
        this.amount = amount;
        this.month = month;
        this.year = year;
        this.dueDate = dueDate;
        this.paymentDate = paymentDate;
        this.method = method;
        this.notes = notes;
        this.status = status;
        this.type = type;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTenantName() { return tenantName; }
    public String getRoomNumber() { return roomNumber; }
    public double getAmount() { return amount; }
    public String getMonth() { return month; }
    public int getYear() { return year; }
    public int getDueDate() { return dueDate; }
    public String getPaymentDate() { return paymentDate; }
    public String getMethod() { return method; }
    public String getNotes() { return notes; }
    public String getStatus() { return status; }
    public String getType() { return type; }

    // Setter for ID (often needed when fetching from Firestore)
    public void setId(String id) { this.id = id; }
}