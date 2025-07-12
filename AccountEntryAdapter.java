package com.wyt.simpleaccounts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wyt.simpleaccounts.R;
import com.wyt.simpleaccounts.data.entity.AccountEntry;
import com.wyt.simpleaccounts.util.CategoryIcons;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AccountEntryAdapter extends ListAdapter<AccountEntry, AccountEntryAdapter.AccountEntryViewHolder> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(AccountEntry entry);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public AccountEntryAdapter() {
        super(new DiffUtil.ItemCallback<AccountEntry>() {
            @Override
            public boolean areItemsTheSame(@NonNull AccountEntry oldItem, @NonNull AccountEntry newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull AccountEntry oldItem, @NonNull AccountEntry newItem) {
                return oldItem.getAmount() == newItem.getAmount() &&
                        oldItem.getType().equals(newItem.getType()) &&
                        oldItem.getCategory().equals(newItem.getCategory()) &&
                        oldItem.getDescription().equals(newItem.getDescription()) &&
                        oldItem.getDate().equals(newItem.getDate());
            }
        });
    }

    @NonNull
    @Override
    public AccountEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account_entry, parent, false);
        return new AccountEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountEntryViewHolder holder, int position) {
        AccountEntry entry = getItem(position);
        holder.bind(entry);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(entry);
            }
        });
    }

    static class AccountEntryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryIcon;
        private final TextView categoryText;
        private final TextView amountText;
        private final TextView descriptionText;
        private final TextView dateText;
        private final Context context;

        public AccountEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            categoryText = itemView.findViewById(R.id.categoryText);
            amountText = itemView.findViewById(R.id.amountText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dateText = itemView.findViewById(R.id.dateText);
        }

        public void bind(AccountEntry entry) {
            // 设置类别图标
            categoryIcon.setImageResource(CategoryIcons.getIconResourceForCategory(entry.getCategory()));
            categoryIcon.setColorFilter(ContextCompat.getColor(context, 
                CategoryIcons.getColorResourceForCategory(entry.getCategory())));
            
            // 设置类别文本
            categoryText.setText(entry.getCategory());
            
            // 设置金额
            String amountPrefix = entry.getType().equals("收入") ? "+ ¥" : "- ¥";
            amountText.setText(String.format(Locale.getDefault(), 
                "%s%.2f", amountPrefix, entry.getAmount()));
            amountText.setTextColor(ContextCompat.getColor(context,
                entry.getType().equals("收入") ? 
                R.color.income_green : R.color.expense_red));
            
            // 设置描述和日期
            descriptionText.setText(entry.getDescription());
            dateText.setText(dateFormat.format(entry.getDate()));
        }
    }
} 