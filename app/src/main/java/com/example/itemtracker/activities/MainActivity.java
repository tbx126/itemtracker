package com.example.itemtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itemtracker.R;
import com.example.itemtracker.adapters.ItemAdapter;
import com.example.itemtracker.database.AppDatabase;
import com.example.itemtracker.models.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<Item> itemList;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据库
        db = AppDatabase.getInstance(this);

        // 设置 RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化空数据列表
        itemList = new ArrayList<>();

        // 设置适配器 - 将 this 作为 OnItemClickListener 传递
        adapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(adapter);

        // 加载物品数据
        loadItems();

        // 设置添加按钮点击事件
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到添加物品页面
                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新物品列表
        loadItems();
    }

    private void loadItems() {
        // 在后台线程中从数据库加载物品
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // 从数据库获取所有物品
            List<Item> items = db.itemDao().getAllItems();

            // 在 UI 线程更新 RecyclerView
            runOnUiThread(() -> {
                itemList.clear();
                itemList.addAll(items);
                adapter.notifyDataSetChanged();

                // 如果没有物品，显示空视图
                findViewById(R.id.empty_view).setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // 处理设置菜单点击
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 实现 OnItemClickListener 接口中的方法
    @Override
    public void onItemClick(Item item) {
        // 跳转到物品详情页面
        Intent intent = new Intent(this, ItemDetailActivity.class);
        intent.putExtra("item_id", item.getId());
        startActivity(intent);
    }
}
