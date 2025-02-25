// app/src/main/java/com/example/itemtracker/activities/MainActivity.java
package com.example.itemtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.itemtracker.R;
import com.example.itemtracker.adapters.ItemAdapter;
import com.example.itemtracker.db.DatabaseHelper;
import com.example.itemtracker.models.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private DatabaseHelper dbHelper;
    private TextView summaryTotalValue, summaryItemCount, summaryDailyCost, summaryActiveItems;
    private DecimalFormat decimalFormat = new DecimalFormat("¥#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取数据库助手实例
        dbHelper = DatabaseHelper.getInstance(this);

        // 初始化视图
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 摘要信息视图
        summaryTotalValue = findViewById(R.id.summary_total_value);
        summaryItemCount = findViewById(R.id.summary_item_count);
        summaryDailyCost = findViewById(R.id.summary_daily_cost);
        summaryActiveItems = findViewById(R.id.summary_active_items);

        // 设置添加按钮
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });

        // 加载数据
        loadItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次返回主界面时重新加载数据，以确保数据是最新的
        loadItems();
    }

    private void loadItems() {
        // 从数据库获取物品列表
        itemList = dbHelper.getAllItems();

        // 设置适配器
        adapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(adapter);

        // 更新摘要信息
        updateSummary();
    }

    private void updateSummary() {
        // 更新摘要面板数据
        summaryTotalValue.setText(decimalFormat.format(dbHelper.getTotalAssetValue()));
        summaryItemCount.setText(String.valueOf(dbHelper.getItemCount()));
        summaryDailyCost.setText(decimalFormat.format(dbHelper.getAverageDailyCost()) + "/日");
        summaryActiveItems.setText(String.valueOf(dbHelper.getActiveItemCount()));
    }
}
