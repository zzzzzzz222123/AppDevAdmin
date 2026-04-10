package com.example.appdevadmin;

import com.google.firebase.Timestamp;

public class ActivityLogModel {
    private String title;
    private String details;
    private com.google.firebase.Timestamp timestamp;
    private String type; // This is the key field!

    public ActivityLogModel() {} // Required

    public String getTitle() { return title; }
    public String getDetails() { return details; }
    public com.google.firebase.Timestamp getTimestamp() { return timestamp; }
    public String getType() { return type; }
}