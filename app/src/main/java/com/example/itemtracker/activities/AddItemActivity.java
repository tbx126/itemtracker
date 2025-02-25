package com.example.itemtracker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.itemtracker.R;
import com.example.itemtracker.db.DatabaseHelper;
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
    private DatabaseHelper dbHelper;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // 初始化数据库助手
        dbHelper = DatabaseHelper.getInstance(this);

        // 初始化视图
        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etPurchaseDate = findViewById(R.id.et_purchase_date);
        btnSave = findViewById(R.id.btn_save);
        ivItemImage = findViewById(R.id.iv_item_image);

        // 初始化日历
        calendar = Calendar.getInstance();

        // 设置购买日期选择器
        etPurchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddItemActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // 设置图片选择
        ivItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // 设置保存按钮
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        }
    };

    private void updateDateLabel() {
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
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
        }
    }

    private void saveItem() {
        String name = etName.getText().toString();
        String priceStr = etPrice.getText().toString();
        String date = etPurchaseDate.getText().toString();

        if (name.isEmpty() || priceStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);

            // 创建新物品对象
            Item item = new Item(name, price, date);

            // 设置图片URI
            if (selectedImageUri != null) {
                item.setImageUri(selectedImageUri.toString());
            }

            // 保存到数据库
            long id = dbHelper.addItem(item);

            if (id > 0) {
                Toast.makeText(this, "物品已成功添加", Toast.LENGTH_SHORT).show();
                finish(); // 返回主界面
            } else {
                Toast.makeText(this, "添加物品失败", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的价格", Toast.LENGTH_SHORT).show();
        }
    }
}
