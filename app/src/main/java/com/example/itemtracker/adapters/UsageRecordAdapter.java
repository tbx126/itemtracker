// app/src/main/java/com/example/itemtracker/adapters/UsageRecordAdapter.java
package com.example.itemtracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.itemtracker.R;
import com.example.itemtracker.models.UsageRecord;
import java.util.List;

public class UsageRecordAdapter extends RecyclerView.Adapter<UsageRecordAdapter.ViewHolder> {

    private List<UsageRecord> recordList;

    public UsageRecordAdapter(List<UsageRecord> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usage_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsageRecord record = recordList.get(position);
        holder.tvDate.setText(record.getDate());
        holder.tvNote.setText(record.getNote());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvNote = itemView.findViewById(R.id.tv_note);
        }
    }
}
