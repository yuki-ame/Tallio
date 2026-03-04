package com.yuvraj.tallio_demo.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuvraj.tallio_demo.R;
import com.yuvraj.tallio_demo.database.DBHelper;
import com.yuvraj.tallio_demo.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * HistoryFragment.java - TRANSACTION HISTORY TAB
 *
 * Shows all transactions in a RecyclerView.
 * Supports: search by name/category, delete individual, clear all.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView      = view.findViewById(R.id.recycler_transactions);
        EditText searchBox = view.findViewById(R.id.et_search);
        Button btnClearAll = view.findViewById(R.id.btn_clear_all);

        dbHelper = new DBHelper(getContext());

        loadTransactions();

        // Live search
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                filterTransactions(s.toString());
            }
        });

        // Clear all with confirmation dialog
        btnClearAll.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Clear All Data")
                        .setMessage("Delete all transactions? This cannot be undone.")
                        .setPositiveButton("Yes, Delete All", (dialog, which) -> {
                            dbHelper.deleteAllTransactions();
                            loadTransactions();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTransactions(); // Refresh when coming back from Add tab
    }

    public void loadTransactions() {
        allTransactions = dbHelper.getAllTransactions();

        if (adapter == null) {
            // First time: create adapter and set it
            adapter = new TransactionAdapter(allTransactions, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        } else {
            // Already exists: just update the list
            adapter.updateList(allTransactions);
        }
    }

    private void filterTransactions(String query) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : allTransactions) {
            if (t.getMerchantName().toLowerCase().contains(query.toLowerCase()) ||
                    t.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(t);
            }
        }
        adapter.updateList(filtered);
    }

    // Called by adapter when user taps Delete on a row
    public void deleteTransaction(int transactionId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Transaction")
                .setMessage("Delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteTransaction(transactionId);
                    loadTransactions();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}