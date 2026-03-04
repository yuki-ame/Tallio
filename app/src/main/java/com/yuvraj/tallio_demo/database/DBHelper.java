package com.yuvraj.tallio_demo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yuvraj.tallio_demo.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * DBHelper.java - OUR SQLITE DATABASE HELPER
 *
 * SQLiteOpenHelper is the standard Android class for managing a local SQLite database.
 * We extend it and override two methods:
 *
 * 1. onCreate()   - Called ONCE when the database is first created on the device.
 *                   We write our CREATE TABLE SQL here.
 *
 * 2. onUpgrade()  - Called when we increase the DATABASE_VERSION number.
 *                   Used to handle schema changes (e.g., adding a new column).
 *
 * CRUD Operations we implement:
 * C - insertTransaction()
 * R - getAllTransactions(), getTransactionById()
 * U - updateTransaction()
 * D - deleteTransaction(), deleteAllTransactions()
 */
public class DBHelper extends SQLiteOpenHelper {

    // Database info
    private static final String DATABASE_NAME = "tallio.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names - using constants avoids typos
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COL_ID = "id";
    public static final String COL_MERCHANT = "merchant_name";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_CATEGORY = "category";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_NOTE = "note";
    public static final String COL_SOURCE = "source";

    // SQL to create our transactions table
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                    COL_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_MERCHANT  + " TEXT, " +
                    COL_AMOUNT    + " REAL, " +
                    COL_CATEGORY  + " TEXT, " +
                    COL_TIMESTAMP + " INTEGER, " +
                    COL_NOTE      + " TEXT, " +
                    COL_SOURCE    + " TEXT" +
                    ")";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This runs when the app is installed for the first time
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If we change the schema, drop the old table and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    // ==================== CREATE ====================
    /**
     * INSERT: Adds a new transaction to the database.
     * ContentValues is like a HashMap - we put column name → value pairs.
     * Returns the row ID of the newly inserted row (-1 if failed).
     */
    public long insertTransaction(Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MERCHANT,  t.getMerchantName());
        values.put(COL_AMOUNT,    t.getAmount());
        values.put(COL_CATEGORY,  t.getCategory());
        values.put(COL_TIMESTAMP, t.getTimestamp());
        values.put(COL_NOTE,      t.getNote());
        values.put(COL_SOURCE,    t.getSource());
        long id = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return id;
    }

    // ==================== READ ====================
    /**
     * SELECT ALL: Returns every transaction, newest first.
     * Cursor is like a pointer that moves through query results row by row.
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Raw SQL query - ORDER BY timestamp DESC means newest first
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + COL_TIMESTAMP + " DESC",
                null
        );

        // Move cursor through each row
        if (cursor.moveToFirst()) {
            do {
                Transaction t = cursorToTransaction(cursor);
                list.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * SELECT BY MONTH: Returns transactions for a specific month.
     * monthYear format: "03-2026"
     */
    public List<Transaction> getTransactionsByMonth(String monthYear) {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TRANSACTIONS +
                        " WHERE strftime('%m-%Y', datetime(" + COL_TIMESTAMP + "/1000, 'unixepoch')) = ?" +
                        " ORDER BY " + COL_TIMESTAMP + " DESC",
                new String[]{monthYear}
        );

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToTransaction(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * SELECT ONE: Get a single transaction by its ID.
     * Used when editing a transaction.
     */
    public Transaction getTransactionById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_TRANSACTIONS + " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        Transaction t = null;
        if (cursor.moveToFirst()) {
            t = cursorToTransaction(cursor);
        }
        cursor.close();
        db.close();
        return t;
    }

    /**
     * SUM BY CATEGORY: Total spending in a category for a given month.
     */
    public double getSumByCategory(String category, String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                        " WHERE " + COL_CATEGORY + " = ?" +
                        " AND strftime('%m-%Y', datetime(" + COL_TIMESTAMP + "/1000, 'unixepoch')) = ?",
                new String[]{category, monthYear}
        );
        double sum = 0;
        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return sum;
    }

    /**
     * TOTAL SPENT: Sum of all transactions for a given month.
     */
    public double getTotalSpentByMonth(String monthYear) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                        " WHERE strftime('%m-%Y', datetime(" + COL_TIMESTAMP + "/1000, 'unixepoch')) = ?",
                new String[]{monthYear}
        );
        double sum = 0;
        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return sum;
    }

    // ==================== UPDATE ====================
    /**
     * UPDATE: Modifies an existing transaction.
     * We identify WHICH row to update using the transaction's ID.
     */
    public int updateTransaction(Transaction t) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MERCHANT,  t.getMerchantName());
        values.put(COL_AMOUNT,    t.getAmount());
        values.put(COL_CATEGORY,  t.getCategory());
        values.put(COL_TIMESTAMP, t.getTimestamp());
        values.put(COL_NOTE,      t.getNote());
        values.put(COL_SOURCE,    t.getSource());

        // "id = ?" means only update the row with this specific ID
        int rows = db.update(TABLE_TRANSACTIONS, values, COL_ID + " = ?",
                new String[]{String.valueOf(t.getId())});
        db.close();
        return rows;
    }

    // ==================== DELETE ====================
    /**
     * DELETE ONE: Removes a single transaction by ID.
     */
    public void deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    /**
     * DELETE ALL: Clears the entire table.
     */
    public void deleteAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TRANSACTIONS);
        db.close();
    }

    // ==================== HELPER ====================
    /**
     * Converts the current cursor row into a Transaction object.
     * cursor.getColumnIndexOrThrow() finds the column index by name.
     */
    private Transaction cursorToTransaction(Cursor cursor) {
        return new Transaction(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_MERCHANT)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_SOURCE))
        );
    }
}