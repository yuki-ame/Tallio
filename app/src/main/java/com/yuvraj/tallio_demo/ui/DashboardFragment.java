package com.yuvraj.tallio_demo.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.yuvraj.tallio_demo.R;
import com.yuvraj.tallio_demo.database.DBHelper;
import com.yuvraj.tallio_demo.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * DashboardFragment.java - HOME SCREEN
 * Updated with dynamic theme colors for PieChart.
 */
public class DashboardFragment extends Fragment {

    private TextView tvTotalSpent, tvMoneyLeft, tvBudgetStatus;
    private PieChart pieChart;
    private DBHelper dbHelper;
    private static final String[] CATEGORIES = {"Food", "Travel", "Academic", "Entertainment", "Other"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvTotalSpent   = view.findViewById(R.id.tv_total_spent);
        tvMoneyLeft    = view.findViewById(R.id.tv_money_left);
        tvBudgetStatus = view.findViewById(R.id.tv_budget_status);
        pieChart       = view.findViewById(R.id.pie_chart);

        dbHelper = new DBHelper(getContext());

        Button btnSample = view.findViewById(R.id.btn_add_sample);
        btnSample.setOnClickListener(v -> addSampleData());

        loadDashboard();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void loadDashboard() {
        String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

        SharedPreferences prefs = requireActivity().getSharedPreferences("Tallio", 0);
        double budget = prefs.getFloat("monthly_budget", 20000f);

        double totalSpent = dbHelper.getTotalSpentByMonth(currentMonth);
        double moneyLeft  = budget - totalSpent;
        double percentUsed = budget > 0 ? (totalSpent / budget) * 100 : 0;

        tvTotalSpent.setText("₹" + String.format("%.0f", totalSpent));
        tvMoneyLeft.setText("₹" + String.format("%.0f", moneyLeft));

        if (percentUsed >= 90) {
            tvBudgetStatus.setText("⚠️ Almost out! " + String.format("%.0f%%", percentUsed) + " used");
        } else if (percentUsed >= 70) {
            tvBudgetStatus.setText("Stay mindful. " + String.format("%.0f%%", percentUsed) + " used");
        } else {
            tvBudgetStatus.setText("You're on track! " + String.format("%.0f%%", percentUsed) + " used");
        }

        setupPieChart(currentMonth);
    }

    private void setupPieChart(String currentMonth) {
        List<PieEntry> entries = new ArrayList<>();
        for (String category : CATEGORIES) {
            double sum = dbHelper.getSumByCategory(category, currentMonth);
            if (sum > 0) {
                entries.add(new PieEntry((float) sum, category));
            }
        }

        // 🌓 Detect Theme for dynamic text coloring
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int dynamicTextColor = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) ? Color.WHITE : Color.BLACK;

        if (entries.isEmpty()) {
            pieChart.setNoDataText("No transactions yet this month");
            pieChart.setNoDataTextColor(dynamicTextColor);
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE); // White text inside colored slices looks best

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // ✨ Styling & Theme Integration
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setHoleColor(Color.TRANSPARENT); // Blends with card background
        pieChart.setTransparentCircleRadius(50f);
        pieChart.getDescription().setEnabled(false);

        // 🏷️ Labels & Legend Text Colors
        pieChart.setEntryLabelColor(dynamicTextColor);
        pieChart.setEntryLabelTextSize(11f);

        Legend legend = pieChart.getLegend();
        legend.setTextColor(dynamicTextColor);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(12f);

        pieChart.animateY(800);
        pieChart.invalidate();
    }

    private void addSampleData() {
        dbHelper.deleteAllTransactions();
        long now = System.currentTimeMillis();
        long hour = 3600 * 1000L;

        dbHelper.insertTransaction(new Transaction("Starbucks Coffee", 250, "Food", now, "HDFC Bank SMS", "SMS"));
        dbHelper.insertTransaction(new Transaction("Metro Card Topup", 500, "Travel", now - hour, "SBI Bank SMS", "SMS"));
        dbHelper.insertTransaction(new Transaction("Amazon Books", 899, "Academic", now - 2*hour, "ICICI Bank SMS", "SMS"));
        dbHelper.insertTransaction(new Transaction("Zomato Order", 350, "Food", now - 3*hour, "Axis Bank SMS", "SMS"));
        dbHelper.insertTransaction(new Transaction("Netflix", 649, "Entertainment", now - 24*hour, "Auto detected", "SMS"));
        dbHelper.insertTransaction(new Transaction("College Canteen", 120, "Food", now - 25*hour, "Cash payment", "Manual"));
        dbHelper.insertTransaction(new Transaction("Uber Ride", 180, "Travel", now - 26*hour, "Paytm SMS", "SMS"));
        dbHelper.insertTransaction(new Transaction("Stationery", 200, "Academic", now - 48*hour, "Cash payment", "Manual"));

        loadDashboard();
    }
}