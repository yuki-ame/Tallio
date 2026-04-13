package com.yuvraj.tallio_demo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.yuvraj.tallio_demo.R;
import com.yuvraj.tallio_demo.database.DBHelper;
import com.yuvraj.tallio_demo.model.Transaction;

public class AddTransactionFragment extends Fragment {

    private EditText etMerchant, etAmount, etNote;
    private Spinner spinnerCategory;
    private Button btnSave, btnDelete;
    private TextView tvTitle;
    private DBHelper dbHelper;

    // If editId > 0, we are in EDIT mode; if 0, we are in ADD mode
    private int editId = 0;

    private static final String[] CATEGORIES = {"Food", "Travel", "Academic", "Entertainment", "Other"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        etMerchant       = view.findViewById(R.id.et_merchant);
        etAmount         = view.findViewById(R.id.et_amount);
        etNote           = view.findViewById(R.id.et_note);
        spinnerCategory  = view.findViewById(R.id.spinner_category);
        btnSave          = view.findViewById(R.id.btn_save);
        btnDelete        = view.findViewById(R.id.btn_delete);
        tvTitle          = view.findViewById(R.id.tv_form_title);

        dbHelper = new DBHelper(getContext());

        // Set up the category dropdown (Spinner)
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                CATEGORIES
        );
        spinnerCategory.setAdapter(spinnerAdapter);

        // Check if we were passed an ID to edit
        if (getArguments() != null) {
            editId = getArguments().getInt("edit_id", 0);
        }

        if (editId > 0) {
            // EDIT MODE: Load existing transaction data into the form
            loadTransactionForEdit(editId);
        } else {
            // ADD MODE: Fresh empty form
            tvTitle.setText("Add Manual Transaction");
            btnDelete.setVisibility(View.GONE); // No delete button when adding new
        }

        btnSave.setOnClickListener(v -> saveTransaction());
        btnDelete.setOnClickListener(v -> deleteTransaction());

        return view;
    }

    /**
     * Loads an existing transaction's data into the form fields for editing.
     */
    private void loadTransactionForEdit(int id) {
        Transaction t = dbHelper.getTransactionById(id);
        if (t == null) return;

        tvTitle.setText("Edit Transaction");
        btnSave.setText("Update");
        btnDelete.setVisibility(View.VISIBLE);

        etMerchant.setText(t.getMerchantName());
        etAmount.setText(String.valueOf(t.getAmount()));
        etNote.setText(t.getNote());

        // Set spinner to the correct category
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].equals(t.getCategory())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    /**
     * Handles both INSERT (add mode) and UPDATE (edit mode).
     */
    private void saveTransaction() {
        String merchant  = etMerchant.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String note      = etNote.getText().toString().trim();
        String category  = spinnerCategory.getSelectedItem().toString();

        // Validation
        if (merchant.isEmpty()) {
            etMerchant.setError("Please enter a name");
            return;
        }
        if (amountStr.isEmpty()) {
            etAmount.setError("Please enter an amount");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        if (editId > 0) {
            // UPDATE existing transaction
            Transaction updated = new Transaction(editId, merchant, amount, category,
                    System.currentTimeMillis(), note, "Manual");
            dbHelper.updateTransaction(updated);
            Toast.makeText(getContext(), "Transaction updated!", Toast.LENGTH_SHORT).show();
        } else {
            // INSERT new transaction
            Transaction newTransaction = new Transaction(merchant, amount, category,
                    System.currentTimeMillis(), note, "Manual");
            dbHelper.insertTransaction(newTransaction);
            Toast.makeText(getContext(), "Transaction saved!", Toast.LENGTH_SHORT).show();
            clearForm();
        }
    }

    private void deleteTransaction() {
        dbHelper.deleteTransaction(editId);
        Toast.makeText(getContext(), "Transaction deleted!", Toast.LENGTH_SHORT).show();
        // Go back to History tab
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HistoryFragment())
                .commit();
    }

    private void clearForm() {
        etMerchant.setText("");
        etAmount.setText("");
        etNote.setText("");
        spinnerCategory.setSelection(0);
    }
}