// app/src/main/java/com/example/itemtracker/models/UsageRecord.java
package com.example.itemtracker.models;

public class UsageRecord {
    private long id;
    private long itemId;
    private String date;
    private String note;

    public UsageRecord() {
    }

    public UsageRecord(long itemId, String date, String note) {
        this.itemId = itemId;
        this.date = date;
        this.note = note;
    }

    public UsageRecord(long id, long itemId, String date, String note) {
        this.id = id;
        this.itemId = itemId;
        this.date = date;
        this.note = note;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
