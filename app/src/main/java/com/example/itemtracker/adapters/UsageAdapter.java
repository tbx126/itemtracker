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

public class UsageAdapter extends RecyclerView.Adapter<UsageAdapter.UsageViewHolder> {

    private List<UsageRecord> usageList;

    public UsageAdapter(List<UsageRecord> usageList) {
        this.usageList = usageList;
    }

    @NonNull
    @Override
    public UsageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.usage_record_item, parent, false);
        return new UsageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsageViewHolder holder, int position) {
        UsageRecord record = usageList.get(position);
        holder.usageDate.setText(record.getDate());
        holder.usageNote.setText(record.getNote());
    }

    @Override
    public int getItemCount() {
        return usageList.size();
    }

    static class UsageViewHolder extends RecyclerView.ViewHolder {
        TextView usageDate, usageNote;

        UsageViewHolder(@NonNull View itemView) {
            super(itemView);
            usageDate = itemView.findViewById(R.id.usage_date);
            usageNote = itemView.findViewById(R.id.usage_note);
        }
    }
}
