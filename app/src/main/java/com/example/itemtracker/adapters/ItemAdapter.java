package com.example.itemtracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.itemtracker.R;
import com.example.itemtracker.activities.ItemDetailActivity;
import com.example.itemtracker.models.Item;
import java.text.DecimalFormat;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;
    private Context context;
    private DecimalFormat decimalFormat = new DecimalFormat("¥#,##0.00");

    public ItemAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(decimalFormat.format(item.getPrice()));
        holder.tvPurchaseDate.setText("购于: " + item.getPurchaseDate());

        // 设置物品图片
        if (item.getImageUriString() != null && !item.getImageUriString().isEmpty()) {
            try {
                Uri imageUri = Uri.parse(item.getImageUriString());

                // 使用Glide安全加载图片
                Glide.with(context)
                        .load(imageUri)
                        .error(R.drawable.ic_default_item)
                        .into(holder.ivItemImage);
            } catch (Exception e) {
                Log.e("ItemAdapter", "Error loading image: " + e.getMessage());
                holder.ivItemImage.setImageResource(R.drawable.ic_default_item);
            }
        } else {
            holder.ivItemImage.setImageResource(R.drawable.ic_default_item);
        }

        // 设置物品激活状态
        if (item.isActive()) {
            holder.tvStatus.setText("已激活");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.tvStatus.setText("已停用");
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.gray));
        }

        // 设置卡片点击事件
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra("item_id", item.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivItemImage;
        TextView tvName, tvPrice, tvPurchaseDate, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPurchaseDate = itemView.findViewById(R.id.tv_purchase_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
