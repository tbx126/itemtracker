package com.example.itemtracker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.itemtracker.R;
import com.example.itemtracker.adapters.UsageRecordAdapter;
import com.example.itemtracker.db.DatabaseHelper;
import com.example.itemtracker.models.Item;
import com.example.itemtracker.models.UsageRecord;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView tvName, tvPrice, tvPurchaseDate, tvDaysOwned, tvCostPerDay;
    private ImageView ivItemImage;
    private Switch switchStatus;
    private Button btnLogUsage, btnChangeImage;
    private RecyclerView rvUsageRecords;
    private UsageRecordAdapter usageAdapter;
    private DatabaseHelper dbHelper;
    private Item item;
    private long itemId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private DecimalFormat decimalFormat = new DecimalFormat("¥#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // 初始化工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("物品详情");

        // 初始化数据库助手
        dbHelper = DatabaseHelper.getInstance(this);

        // 获取传入的物品ID
        itemId = getIntent().getLongExtra("item_id", -1);
        if (itemId == -1) {
            Toast.makeText(this, "物品不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化视图
        tvName = findViewById(R.id.tv_name);
        tvPrice = findViewById(R.id.tv_price);
        tvPurchaseDate = findViewById(R.id.tv_purchase_date);
        tvDaysOwned = findViewById(R.id.tv_days_owned);
        tvCostPerDay = findViewById(R.id.tv_cost_per_day);
        ivItemImage = findViewById(R.id.iv_item_image);
        switchStatus = findViewById(R.id.switch_status);
        btnLogUsage = findViewById(R.id.btn_log_usage);
        btnChangeImage = findViewById(R.id.btn_change_image);
        rvUsageRecords = findViewById(R.id.rv_usage_records);
        rvUsageRecords.setLayoutManager(new LinearLayoutManager(this));

        // 加载物品数据
        loadItemData();

        // 设置开关状态变更监听器
        switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setActive(isChecked);
            dbHelper.updateItem(item);
            updateStatusText();
        });

        // 设置记录使用按钮点击事件
        btnLogUsage.setOnClickListener(v -> showLogUsageDialog());

        // 设置更改图片按钮点击事件
        btnChangeImage.setOnClickListener(v -> openImagePicker());
    }

    private void loadItemData() {
        // 从数据库获取物品
        item = dbHelper.getItem(itemId);
        if (item == null) {
            Toast.makeText(this, "物品不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 设置物品信息
        tvName.setText(item.getName());
        tvPrice.setText(decimalFormat.format(item.getPrice()));
        tvPurchaseDate.setText(item.getPurchaseDate());
        tvDaysOwned.setText(item.getDaysOwned() + " 天");
        tvCostPerDay.setText(decimalFormat.format(item.getCostPerDay()) + "/天");

        // 设置物品图片
        loadItemImage();

        // 设置开关状态
        switchStatus.setChecked(item.isActive());
        updateStatusText();

        // 加载使用记录
        loadUsageRecords();
    }

    private void loadItemImage() {
        if (item.getImageUriString() != null && !item.getImageUriString().isEmpty()) {
            try {
                Uri imageUri = Uri.parse(item.getImageUriString());
                Glide.with(this)
                        .load(imageUri)
                        .error(R.drawable.ic_default_item)
                        .into(ivItemImage);
            } catch (Exception e) {
                ivItemImage.setImageResource(R.drawable.ic_default_item);
            }
        } else {
            ivItemImage.setImageResource(R.drawable.ic_default_item);
        }
    }

    private void updateStatusText() {
        switchStatus.setText(item.isActive() ? "已激活" : "已停用");
    }

    private void loadUsageRecords() {
        // 获取物品的所有使用记录
        List<UsageRecord> usageRecords = dbHelper.getAllUsageRecords(itemId);
        usageAdapter = new UsageRecordAdapter(usageRecords);
        rvUsageRecords.setAdapter(usageAdapter);
    }

    private void showLogUsageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_log_usage, null);
        final EditText etNote = view.findViewById(R.id.et_note);

        builder.setTitle("记录使用")
                .setView(view)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String note = etNote.getText().toString();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String date = sdf.format(new Date());

                        UsageRecord record = new UsageRecord(itemId, date, note);
                        long id = dbHelper.addUsageRecord(record);

                        if (id > 0) {
                            Toast.makeText(ItemDetailActivity.this, "使用记录已添加", Toast.LENGTH_SHORT).show();
                            loadUsageRecords(); // 重新加载使用记录
                        } else {
                            Toast.makeText(ItemDetailActivity.this, "添加使用记录失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            // 获取永久读取权限
            try {
                getContentResolver().takePersistableUriPermission(selectedImageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e) {
                // 有些URI可能不支持持久权限
            }

            // 使用Glide加载图片
            Glide.with(this)
                    .load(selectedImageUri)
                    .error(R.drawable.ic_default_item)
                    .into(ivItemImage);

            // 更新物品图片URI
            item.setImageUri(selectedImageUri.toString());

            // 保存到数据库
            dbHelper.updateItem(item);
            Toast.makeText(this, "图片已更新", Toast.LENGTH_SHORT).show();
        }
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
            finish();
            return true;
        } else if (id == R.id.action_edit) {
            // 跳转到编辑界面
            Intent intent = new Intent(this, EditItemActivity.class);
            intent.putExtra("item_id", itemId);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_delete) {
            showDeleteConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除物品")
                .setMessage("确定要删除这个物品吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteItem(itemId);
                        Toast.makeText(ItemDetailActivity.this, "物品已删除", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新加载物品数据，以防在编辑界面修改了
        loadItemData();
    }
}
