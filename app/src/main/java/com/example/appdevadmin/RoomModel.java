package com.example.appdevadmin;

public class RoomModel {
    private String id;
    private String roomNumber;
    private String roomType;
    private String floor;
    private String status;
    private String imageUrl;
    private double monthlyRent;

    // Required empty constructor for Firestore
    public RoomModel() {}

    // OLD CONSTRUCTOR (6 parameters)
    // Keeping this prevents errors in roomFragment and addNewUserFragment
    public RoomModel(String id, String roomNumber, String roomType, String floor, double monthlyRent, String status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.floor = floor;
        this.monthlyRent = monthlyRent;
        this.status = status;
        this.imageUrl = ""; // Default to empty string
    }

    // NEW CONSTRUCTOR (7 parameters)
    // Use this when you want to include the image
    public RoomModel(String id, String roomNumber, String roomType, String floor, double monthlyRent, String status, String imageUrl) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.floor = floor;
        this.monthlyRent = monthlyRent;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getImageUrl() { return imageUrl; }
    public String getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public String getFloor() { return floor; }
    public double getMonthlyRent() { return monthlyRent; }
    public String getStatus() { return status; }

    // Setters
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setId(String id) { this.id = id; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setFloor(String floor) { this.floor = floor; }
    public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }
    public void setStatus(String status) { this.status = status; }
}