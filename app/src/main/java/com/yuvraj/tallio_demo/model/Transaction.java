package com.yuvraj.tallio_demo.model;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction {

    private int id;
    private String merchantName;
    private double amount;
    private String category;
    private long timestamp;
    private String note;
    private String source;

    // Constructor for new transactions (no ID yet)
    public Transaction(String merchantName, double amount, String category, long timestamp, String note, String source) {
        this.merchantName = merchantName;
        this.amount = amount;
        this.category = category;
        this.timestamp = timestamp;
        this.note = note;
        this.source = source;
    }

    public String getDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date(this.timestamp);
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    // Constructor with ID (used when reading from database)
    public Transaction(int id, String merchantName, double amount, String category, long timestamp, String note, String source) {
        this.id = id;
        this.merchantName = merchantName;
        this.amount = amount;
        this.category = category;
        this.timestamp = timestamp;
        this.note = note;
        this.source = source;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}