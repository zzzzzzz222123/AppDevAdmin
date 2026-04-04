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
    private String role;

    public UserModel() {}

    public UserModel(String uid, String fullName, String email, String phone,
                     String roomNumber, String leaseStart, String leaseEnd,
                     String status, double monthlyRent, int rentDueDate,
                     String emergencyName, String relationship,
                     String emergencyPhone, String role) {
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
        this.role = role;
    }

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
    public String getRole() { return role; }
}