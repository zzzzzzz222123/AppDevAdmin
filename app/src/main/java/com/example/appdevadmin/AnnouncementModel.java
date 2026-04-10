package com.example.appdevadmin;

import com.google.firebase.Timestamp;

public class AnnouncementModel {
    private String title;
    private String content;
    private String recipient;
    private Timestamp timestamp;

    public AnnouncementModel() {} // Required for Firebase

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getRecipient() { return recipient; }
    public Timestamp getTimestamp() { return timestamp; }
}