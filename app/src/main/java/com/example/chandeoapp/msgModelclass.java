package com.example.chandeoapp;

public class msgModelclass {
    String id;
    String message;
    String senderid;
    long timeStamp;


    public msgModelclass() {
    }

    public msgModelclass(String id,String message, String senderid, long timeStamp) {
        this.message = message;
        this.id = id;
        this.senderid = senderid;
        this.timeStamp = timeStamp;
    }

    public msgModelclass(String message, String senderid, long timeStamp) {
        this.message = message;
        this.senderid = senderid;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "msgModelclass{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", senderid='" + senderid + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
