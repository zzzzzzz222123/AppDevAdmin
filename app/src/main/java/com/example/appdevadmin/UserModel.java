package com.example.appdevadmin;

public class UserModel {
    private String uid;
    private String fullName;
    private String email;
    private String phone;
    private String roomNumber;
    private String leaseStart;
    private String leaseEnd;
    private String status;
    private double monthlyRent;
    private int rentDueDate;
    private String emergencyName;
    private String relationship;
    private String emergencyPhone;
    private String roomId;
    private String role;

    // Empty constructor for Firebase
    public UserModel() {}

    // Full constructor
    public UserModel(String uid, String fullName, String email, String phone,
                     String roomNumber, String leaseStart, String leaseEnd,
                     String status, double monthlyRent, int rentDueDate,
                     String emergencyName, String relationship,
                     String emergencyPhone, String roomId, String role) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roomNumber = roomNumber;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
        this.status = status;
        this.monthlyRent = monthlyRent;
        this.rentDueDate = rentDueDate;
        this.emergencyName = emergencyName;
        this.relationship = relationship;
        this.emergencyPhone = emergencyPhone;
        this.roomId = roomId;
        this.role = role;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRoomNumber() { return roomNumber; }
    public String getLeaseStart() { return leaseStart; }
    public String getLeaseEnd() { return leaseEnd; }
    public String getStatus() { return status; }
    public double getMonthlyRent() { return monthlyRent; }
    public int getRentDueDate() { return rentDueDate; }
    public String getEmergencyName() { return emergencyName; }
    public String getRelationship() { return relationship; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public String getRoomId() { return roomId; }
    public String getRole() { return role; }

    // --- SETTERS (ADDED THESE TO FIX YOUR ERROR) ---
    public void setUid(String uid) { this.uid = uid; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setLeaseStart(String leaseStart) { this.leaseStart = leaseStart; }
    public void setLeaseEnd(String leaseEnd) { this.leaseEnd = leaseEnd; }
    public void setStatus(String status) { this.status = status; }
    public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }
    public void setRentDueDate(int rentDueDate) { this.rentDueDate = rentDueDate; }
    public void setEmergencyName(String emergencyName) { this.emergencyName = emergencyName; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setRole(String role) { this.role = role; }
}