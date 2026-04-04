package com.example.appdevadmin;

public class RoomModel {
    private String id;
    private String roomNumber;
    private String roomType;
    private String floor;
    private double monthlyRent;
    private String status;

    public RoomModel() {} // Required for Firestore

    public RoomModel(String id, String roomNumber, String roomType, String floor, double monthlyRent, String status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.floor = floor;
        this.monthlyRent = monthlyRent;
        this.status = status;
    }

    public String getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public String getFloor() { return floor; }
    public double getMonthlyRent() { return monthlyRent; }
    public String getStatus() { return status; }

    public void setId(String id) { this.id = id; }
}