package com.example.itemtracker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.itemtracker.R;
import com.example.itemtracker.database.AppDatabase;
import com.example.itemtracker.models.Item;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    private EditText etName, etPrice, etPurchaseDate;
    private Button btnSave;
    private ImageView ivItemImage;
    private Uri selectedImageUri;
    private Calendar calendar;
    private AppDatabase db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String DATE_FORMAT = "yyyy-MM-dd"; // 统一的日期格式

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // 启用返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("添加物品");
        }

        // 初始化数据库
        db = AppDatabase.getInstance(this);

        // 初始化视图
        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etPurchaseDate = findViewById(R.id.et_purchase_date);
        btnSave = findViewById(R.id.btn_save);
        ivItemImage = findViewById(R.id.iv_item_image);

        // 初始化日历并设置为当前日期
        calendar = Calendar.getInstance();

        // 默认显示当前日期
        updateDateLabel();

        // 设置购买日期选择器
        etPurchaseDate.setOnClickListener(v -> {
            new DatePickerDialog(AddItemActivity.this, dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 设置图片选择
        ivItemImage.setOnClickListener(v -> openImagePicker());

        // 设置保存按钮
        btnSave.setOnClickListener(v -> saveItem());
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateLabel();
    };

    private void updateDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        etPurchaseDate.setText(sdf.format(calendar.getTime()));
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
            selectedImageUri = data.getData();

            // 获取永久读取权限
            try {
                int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(selectedImageUri, flags);
            } catch (Exception e) {
                // 有些URI可能不支持持久权限
                e.printStackTrace();
            }

            // 使用Glide加载图片
            Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .error(R.drawable.ic_default_item)
                    .into(ivItemImage);
        }
    }

    private void saveItem() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String date = etPurchaseDate.getText().toString().trim();

        // 验证输入
        if (name.isEmpty()) {
            etName.setError("请输入物品名称");
            etName.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            etPrice.setError("请输入价格");
            etPrice.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            etPurchaseDate.setError("请选择日期");
            etPurchaseDate.requestFocus();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etPrice.setError("价格必须大于0");
                etPrice.requestFocus();
                return;
            }

            // 验证日期格式
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                sdf.setLenient(false); // 严格验证日期格式
                sdf.parse(date);
            } catch (Exception e) {
                etPurchaseDate.setError("日期格式无效");
                etPurchaseDate.requestFocus();
                return;
            }

            // 创建新物品对象
            final Item item = new Item();
            item.setName(name);
            item.setPrice(price);
            item.setPurchaseDate(date);

            // 设置图片URI
            if (selectedImageUri != null) {
                item.setImageUriString(selectedImageUri.toString());
            }

            // 在后台线程中保存到数据库
            AppDatabase.databaseWriteExecutor.execute(() -> {
                // 保存到数据库 - 修改这里
                try {
                    db.itemDao().insert(item);

                    // 在UI线程中显示成功结果
                    runOnUiThread(() -> {
                        Toast.makeText(AddItemActivity.this, "物品已成功添加", Toast.LENGTH_SHORT).show();
                        finish(); // 返回主界面
                    });
                } catch (Exception e) {
                    // 在UI线程中显示失败结果
                    runOnUiThread(() -> {
                        Toast.makeText(AddItemActivity.this, "添加物品失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    e.printStackTrace();
                }
            });
        } catch (NumberFormatException e) {
            etPrice.setError("请输入有效的价格");
            etPrice.requestFocus();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
