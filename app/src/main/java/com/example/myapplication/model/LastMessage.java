package com.example.myapplication.model;

public class LastMessage {
    public String chatWithId;
    public String chatWithName;
    public String lastMessage;
    public long timestamp;

    public LastMessage(String chatWithId, String chatWithName, String lastMessage, long timestamp) {
        this.chatWithId = chatWithId;
        this.chatWithName = chatWithName;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }
}
