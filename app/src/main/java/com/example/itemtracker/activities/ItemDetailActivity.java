package com.example.itemtracker.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.itemtracker.R;
import com.example.itemtracker.database.AppDatabase;
import com.example.itemtracker.models.Item;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ItemDetailActivity extends AppCompatActivity {

    private static final int REQUEST_EDIT_ITEM = 1;
    private static final int REQUEST_PICK_IMAGE = 2;

    private long itemId;
    private Item currentItem;
    private AppDatabase db;
    private TextView nameTextView, priceTextView, dateTextView;
    private ImageView itemImageView;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化数据库
        db = AppDatabase.getInstance(this);

        // 初始化视图
        nameTextView = findViewById(R.id.tv_item_name);
        priceTextView = findViewById(R.id.tv_item_price);
        dateTextView = findViewById(R.id.tv_purchase_date);
        itemImageView = findViewById(R.id.iv_item_image);

        // 获取物品ID
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("item_id")) {
            itemId = intent.getLongExtra("item_id", -1);
            loadItem();
        } else {
            Toast.makeText(this, "无法加载物品信息", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 设置点击图片更换图片
        itemImageView.setOnClickListener(v -> selectNewImage());

        // 设置点击日期更改日期
        dateTextView.setOnClickListener(v -> showDatePickerDialog());

        // 初始化日历对象
        calendar = Calendar.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_edit) {
            editItem();
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadItem() {
        if (itemId == -1) return;

        // 在后台线程中从数据库加载物品
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Item item = db.itemDao().getItemById(itemId);
            runOnUiThread(() -> {
                if (item != null) {
                    currentItem = item;
                    updateUI(currentItem);
                } else {
                    Toast.makeText(this, "未找到物品", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        });
    }

    private void updateUI(Item item) {
        if (item == null) return;

        // 设置标题
        setTitle(item.getName());

        // 设置物品名称
        nameTextView.setText(item.getName());

        // 设置物品价格
        priceTextView.setText(String.format(Locale.getDefault(), "¥%.2f", item.getPrice()));

        // 设置购买日期
        dateTextView.setText(item.getPurchaseDate());

        // 设置物品图片
        String imageUriString = item.getImageUriString();
        if (imageUriString != null && !imageUriString.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(imageUriString);
                itemImageView.setImageURI(imageUri);
            } catch (Exception e) {
                // 设置默认图片
                itemImageView.setImageResource(R.drawable.ic_default_item);
                e.printStackTrace();
            }
        } else {
            // 没有图片时设置默认图片
            itemImageView.setImageResource(R.drawable.ic_default_item);
        }
    }

    private void editItem() {
        if (currentItem == null) return;

        Intent intent = new Intent(this, EditItemActivity.class);
        intent.putExtra("item_id", itemId);
        startActivityForResult(intent, REQUEST_EDIT_ITEM);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("删除物品")
                .setMessage("确定要删除此物品吗？此操作不可撤销。")
                .setPositiveButton("删除", (dialog, which) -> deleteItem())
                .setNegativeButton("取消", null)
                .show();
    }

    private void deleteItem() {
        if (currentItem == null) return;

        // 在后台线程中删除物品
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                db.itemDao().delete(currentItem);
                runOnUiThread(() -> {
                    Toast.makeText(this, "物品已删除", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "删除失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void selectNewImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private void showDatePickerDialog() {
        if (currentItem == null) return;

        try {
            // 解析当前日期
            String currentDate = currentItem.getPurchaseDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            calendar.setTime(sdf.parse(currentDate));
        } catch (Exception e) {
            // 如果解析失败，使用当前日期
            calendar = Calendar.getInstance();
        }

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDate() {
        if (currentItem == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String newDate = sdf.format(calendar.getTime());

        // 更新日期
        currentItem.setPurchaseDate(newDate);

        // 在后台线程中保存到数据库
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                db.itemDao().update(currentItem);
                runOnUiThread(() -> {
                    updateUI(currentItem);
                    Toast.makeText(this, "日期已更新", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "更新日期失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_EDIT_ITEM) {
                // 编辑物品后刷新数据
                loadItem();
                Toast.makeText(this, "物品已更新", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();

                // 获取永久读取权限
                try {
                    getContentResolver().takePersistableUriPermission(selectedImageUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception e) {
                    // 有些URI可能不支持持久权限
                }

                // 更新物品图片
                if (currentItem != null) {
                    currentItem.setImageUriString(selectedImageUri.toString());

                    // 在后台线程中保存到数据库
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        try {
                            db.itemDao().update(currentItem);
                            runOnUiThread(() -> {
                                updateUI(currentItem);
                                Toast.makeText(this, "图片已更新", Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                Toast.makeText(this, "更新图片失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            }
        }
    }
}
