package com.yuvraj.tallio_demo.model;

/**
 * Transaction.java - THE DATA MODEL
 *
 * A plain Java class (POJO) that represents one expense entry.
 * No Room annotations needed - we use DBHelper instead.
 * Each field maps to a column in our SQLite database table.
 */
public class Transaction {

    private int id;
    private String merchantName;  // e.g., "Zomato Order"
    private double amount;         // e.g., 350.0
    private String category;       // e.g., "Food"
    private long timestamp;        // When the transaction happened
    private String note;           // Optional note from user
    private String source;         // "SMS" or "Manual"

    // Constructor for new transactions (no ID yet - DB assigns it)
    public Transaction(String merchantName, double amount, String category, long timestamp, String note, String source) {
        this.merchantName = merchantName;
        this.amount = amount;
        this.category = category;
        this.timestamp = timestamp;
        this.note = note;
        this.source = source;
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

    // --- Getters and Setters ---
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