package com.yuvraj.tallio_demo.receiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SmsParser.java - REGEX UTILITY
 *
 * Uses Regular Expressions to extract data from bank SMS messages.
 * A Regex is a pattern used to find text that matches a specific format.
 */
public class SmsParser {

    public static boolean isBankSms(String smsBody) {
        String lower = smsBody.toLowerCase();
        return lower.contains("debited") || lower.contains("deducted") ||
                lower.contains("spent") || lower.contains("paid") ||
                lower.contains("rs.") || lower.contains("inr") ||
                lower.contains("upi");
    }

    public static double extractAmount(String smsBody) {
        String[] patterns = {
                "(?:Rs\\.?|INR)\\s*([\\d,]+\\.?\\d*)",
                "([\\d,]+\\.?\\d*)\\s*(?:debited|deducted|spent)"
        };
        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(smsBody);
            if (matcher.find()) {
                String amountStr = matcher.group(1).replace(",", "");
                try {
                    return Double.parseDouble(amountStr);
                } catch (NumberFormatException e) { }
            }
        }
        return 0.0;
    }

    public static String extractMerchant(String smsBody) {
        Pattern pattern = Pattern.compile("(?:at|to|for)\\s+([A-Za-z0-9 &'-]{3,30})", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(smsBody);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Bank Transaction";
    }

    public static String categorize(String merchantName, String smsBody) {
        String combined = (merchantName + " " + smsBody).toLowerCase();
        if (combined.contains("zomato") || combined.contains("swiggy") ||
                combined.contains("cafe") || combined.contains("coffee") ||
                combined.contains("restaurant") || combined.contains("canteen") ||
                combined.contains("food")) return "Food";
        if (combined.contains("metro") || combined.contains("uber") ||
                combined.contains("ola") || combined.contains("rapido") ||
                combined.contains("bus") || combined.contains("irctc")) return "Travel";
        if (combined.contains("amazon") || combined.contains("book") ||
                combined.contains("course") || combined.contains("fee") ||
                combined.contains("stationery") || combined.contains("flipkart")) return "Academic";
        if (combined.contains("netflix") || combined.contains("spotify") ||
                combined.contains("prime") || combined.contains("movie") ||
                combined.contains("game")) return "Entertainment";
        return "Other";
    }
}