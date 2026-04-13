package com.yuvraj.tallio_demo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yuvraj.tallio_demo.R;
import com.yuvraj.tallio_demo.database.DBHelper;
import com.yuvraj.tallio_demo.model.Transaction;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * HistoryFragment.java - TRANSACTION HISTORY TAB
 *
 * Full implementation with CSV Export capability.
 */
public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactions;
    private DBHelper dbHelper;
    private static final int CREATE_FILE_REQUEST_CODE = 101;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recycler_transactions);
        EditText searchBox = view.findViewById(R.id.et_search);
        Button btnClearAll = view.findViewById(R.id.btn_clear_all);

        // ✅ Initialize the Export Button
        ImageButton btnExportCsv = view.findViewById(R.id.btn_export_csv);

        dbHelper = new DBHelper(getContext());

        loadTransactions();

        // Live search logic
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                filterTransactions(s.toString());
            }
        });

        // ✅ Export CSV Logic
        btnExportCsv.setOnClickListener(v -> openFilePicker());

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

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "Tallio_Transactions.csv");
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == android.app.Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                writeCsvToFile(uri);
            }
        }
    }

    private void writeCsvToFile(Uri uri) {
        try {
            ParcelFileDescriptor pfd = requireActivity().getContentResolver().openFileDescriptor(uri, "w");
            if (pfd != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());

                // Header row
                StringBuilder csvContent = new StringBuilder("Date,Merchant,Category,Amount\n");

                // Data rows
                // Inside writeCsvToFile loop
                for (Transaction t : allTransactions) {
                    String merchant = t.getMerchantName().replace(",", " ");
                    String category = t.getCategory().replace(",", " ");
                    String note = (t.getNote() != null) ? t.getNote().replace(",", " ") : "";

                    csvContent.append(t.getDate()).append(",")  // Now this will work!
                            .append(merchant).append(",")
                            .append(category).append(",")
                            .append(t.getAmount()).append(",")
                            .append(note).append("\n");
                }

                fileOutputStream.write(csvContent.toString().getBytes());
                fileOutputStream.close();
                pfd.close();

                Toast.makeText(getContext(), "Transactions Exported Successfully!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Export Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTransactions();
    }

    public void loadTransactions() {
        allTransactions = dbHelper.getAllTransactions();

        if (adapter == null) {
            adapter = new TransactionAdapter(allTransactions, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        } else {
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