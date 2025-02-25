package com.example.itemtracker.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.itemtracker.R;
import com.example.itemtracker.models.Item;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public ItemAdapter(List<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);

        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.format(Locale.getDefault(), "价格: ¥%.2f", item.getPrice()));

        // 计算已拥有天数
        long daysOwned = 0;
        if (item.getPurchaseDate() != null) {
            long purchaseTime = item.getPurchaseDateAsDate().getTime();
            long currentTime = System.currentTimeMillis();
            daysOwned = TimeUnit.MILLISECONDS.toDays(currentTime - purchaseTime) + 1; // +1 包含购买当天
        }
        holder.daysOwnedTextView.setText(String.format(Locale.getDefault(), "已购天数: %d天", daysOwned));

        // 计算日均价格
        double dailyPrice = 0;
        if (daysOwned > 0) {
            dailyPrice = item.getPrice() / daysOwned;
        }
        holder.dailyPriceTextView.setText(String.format(Locale.getDefault(), "日均价格: ¥%.2f/天", dailyPrice));

        // 加载图片
        if (item.getImageUriString() != null && !item.getImageUriString().isEmpty()) {
            try {
                Uri imageUri = Uri.parse(item.getImageUriString());
                Glide.with(holder.itemView.getContext())
                        .load(imageUri)
                        .error(R.drawable.ic_default_item)
                        .into(holder.itemImageView);
            } catch (Exception e) {
                Log.e("ItemAdapter", "Error loading image: " + e.getMessage());
                holder.itemImageView.setImageResource(R.drawable.ic_default_item);
            }
        } else {
            holder.itemImageView.setImageResource(R.drawable.ic_default_item);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView daysOwnedTextView;
        TextView dailyPriceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.iv_item_image);
            nameTextView = itemView.findViewById(R.id.tv_item_name);
            priceTextView = itemView.findViewById(R.id.tv_item_price);
            daysOwnedTextView = itemView.findViewById(R.id.tv_days_owned);
            dailyPriceTextView = itemView.findViewById(R.id.tv_daily_price);
        }
    }
}
