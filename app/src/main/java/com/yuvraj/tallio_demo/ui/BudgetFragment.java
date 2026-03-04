package com.yuvraj.tallio_demo.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.yuvraj.tallio_demo.R;
import com.yuvraj.tallio_demo.database.DBHelper;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        EditText etBudget        = view.findViewById(R.id.et_monthly_budget);
        Button btnSave           = view.findViewById(R.id.btn_save_budget);
        TextView tvBudgetAmount  = view.findViewById(R.id.tv_budget_amount);
        TextView tvSpentAmount   = view.findViewById(R.id.tv_spent_amount);
        TextView tvSpentPercent  = view.findViewById(R.id.tv_spent_percent);
        TextView tvRemaining     = view.findViewById(R.id.tv_remaining_amount);
        ProgressBar progressBar  = view.findViewById(R.id.progress_budget);
        TextView tvPercent       = view.findViewById(R.id.tv_progress_percent);

        SharedPreferences prefs = requireActivity().getSharedPreferences("Tallio", 0);
        DBHelper dbHelper = new DBHelper(getContext());

        // Load current stats
        loadStats(prefs, dbHelper, tvBudgetAmount, tvSpentAmount, tvSpentPercent, tvRemaining, progressBar, tvPercent);

        btnSave.setOnClickListener(v -> {
            String input = etBudget.getText().toString().trim();
            if (input.isEmpty()) {
                etBudget.setError("Please enter a budget amount");
                return;
            }
            float newBudget = Float.parseFloat(input);
            prefs.edit().putFloat("monthly_budget", newBudget).apply();
            Toast.makeText(getContext(), "Budget saved!", Toast.LENGTH_SHORT).show();
            etBudget.setText("");
            loadStats(prefs, dbHelper, tvBudgetAmount, tvSpentAmount, tvSpentPercent, tvRemaining, progressBar, tvPercent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh when coming back to this tab
    }

    private void loadStats(SharedPreferences prefs, DBHelper dbHelper,
                           TextView tvBudget, TextView tvSpent, TextView tvSpentPct,
                           TextView tvRemaining, ProgressBar bar, TextView tvPct) {

        String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
        float budget    = prefs.getFloat("monthly_budget", 20000f);
        double spent    = dbHelper.getTotalSpentByMonth(currentMonth);
        double remaining = budget - spent;
        double percent  = budget > 0 ? (spent / budget) * 100 : 0;

        // Format numbers with commas e.g. ₹20,000
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "IN"));

        tvBudget.setText("₹" + nf.format((int) budget));
        tvSpent.setText("₹" + nf.format((int) spent));
        tvSpentPct.setText(String.format("%.1f%% of budget", percent));
        tvRemaining.setText("₹" + nf.format((int) remaining));

        // Progress bar (0-100)
        int progress = (int) Math.min(percent, 100);
        bar.setProgress(progress);
        tvPct.setText(String.format("%.1f%%", percent));

        // Change progress bar color based on usage
        if (percent >= 90) {
            bar.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFEF4444)); // Red
        } else if (percent >= 70) {
            bar.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFFF59E0B)); // Amber
        } else {
            bar.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFF3B82F6)); // Blue
        }
    }
}