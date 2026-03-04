package com.yuvraj.tallio_demo.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuvraj.tallio_demo.R;
import com.yuvraj.tallio_demo.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * TransactionAdapter.java - RECYCLERVIEW ADAPTER
 *
 * Bridges the Transaction data list and the RecyclerView UI.
 * Each row shows merchant, amount, category, date, and delete button.
 *
 * We pass a reference to HistoryFragment so we can call
 * deleteTransaction() when the delete button is tapped.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private HistoryFragment historyFragment; // Reference to call delete from

    public TransactionAdapter(List<Transaction> transactions, HistoryFragment historyFragment) {
        this.transactions = transactions;
        this.historyFragment = historyFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = transactions.get(position);

        holder.tvMerchant.setText(t.getMerchantName());
        holder.tvAmount.setText("-₹" + String.format("%.0f", t.getAmount()));
        holder.tvCategory.setText(t.getCategory());
        holder.tvSource.setText(t.getSource()); // "SMS" or "Manual"

        // Note (optional)
        if (t.getNote() != null && !t.getNote().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(t.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        // Format timestamp into readable date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd • hh:mm a", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(t.getTimestamp())));

        // Category color
        int color;
        switch (t.getCategory()) {
            case "Food":          color = 0xFFFF6B6B; break;
            case "Travel":        color = 0xFF4ECDC4; break;
            case "Academic":      color = 0xFF9B59B6; break;
            case "Entertainment": color = 0xFFF39C12; break;
            default:              color = 0xFF95A5A6; break;
        }
        holder.tvCategory.setTextColor(color);

        // Delete button - calls back to HistoryFragment
        holder.btnDelete.setOnClickListener(v ->
                historyFragment.deleteTransaction(t.getId())
        );
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateList(List<Transaction> newList) {
        transactions = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMerchant, tvAmount, tvCategory, tvDate, tvNote, tvSource;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMerchant  = itemView.findViewById(R.id.tv_merchant);
            tvAmount    = itemView.findViewById(R.id.tv_amount);
            tvCategory  = itemView.findViewById(R.id.tv_category);
            tvDate      = itemView.findViewById(R.id.tv_date);
            tvNote      = itemView.findViewById(R.id.tv_note);
            tvSource    = itemView.findViewById(R.id.tv_source);
            btnDelete   = itemView.findViewById(R.id.btn_delete);
        }
    }
}