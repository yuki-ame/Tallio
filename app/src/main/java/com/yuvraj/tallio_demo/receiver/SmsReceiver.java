package com.yuvraj.tallio_demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.yuvraj.tallio_demo.database.DBHelper;
import com.yuvraj.tallio_demo.model.Transaction;

public class SmsReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "tallio_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        // Get the SMS format (needed for Android 6+)
        String format = bundle.getString("format");

        for (Object pdu : pdus) {
            SmsMessage smsMessage;

            // Use the format-aware method on Android 6+, fallback on older
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
            } else {
                smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
            }

            if (smsMessage == null) continue;

            String smsBody = smsMessage.getMessageBody();
            if (smsBody == null || smsBody.isEmpty()) continue;

            if (SmsParser.isBankSms(smsBody)) {
                double amount = SmsParser.extractAmount(smsBody);
                String merchant = SmsParser.extractMerchant(smsBody);
                String category = SmsParser.categorize(merchant, smsBody);

                if (amount > 0) {
                    Transaction transaction = new Transaction(
                            merchant, amount, category,
                            System.currentTimeMillis(), smsBody, "SMS"
                    );

                    DBHelper dbHelper = new DBHelper(context);
                    dbHelper.insertTransaction(transaction);

                    showNotification(context, merchant, amount, category);
                }
            }
        }
    }

    private void showNotification(Context context, String merchant, double amount, String category) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Tallio Alerts", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("New " + category + " Expense Detected!")
                .setContentText(merchant + " - ₹" + String.format("%.0f", amount))
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}