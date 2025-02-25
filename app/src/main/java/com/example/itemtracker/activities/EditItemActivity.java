// app/src/main/java/com/example/itemtracker/activities/EditItemActivity.java
package com.example.itemtracker.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.itemtracker.R;
import com.example.itemtracker.db.DatabaseHelper;
import com.example.itemtracker.models.Item;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditItemActivity extends AppCompatActivity {

    private EditText etName, etPrice, etPurchaseDate;
    private Button btnSave;
    private ImageView ivItemImage;
    private Uri selectedImageUri;
    private Calendar calendar;
    private DatabaseHelper dbHelper;
    private Item item;
    private long itemId;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // 初始化工具栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("编辑物品");

        // 初始化数据库助手
        dbHelper = DatabaseHelper.getInstance(this);

        // 获取传入的物品ID
        itemId = getIntent().getLongExtra("item_id", -1);
        if (itemId == -1) {
            Toast.makeText(this, "物品不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 获取物品数据
        item = dbHelper.getItem(itemId);
        if (item == null) {
            Toast.makeText(this, "物品不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化视图
        etName = findViewById(R.id.et_name);
        etPrice = findViewById(R.id.et_price);
        etPurchaseDate = findViewById(R.id.et_purchase_date);
        btnSave = findViewById(R.id.btn_save);
        ivItemImage = findViewById(R.id.iv_item_image);

        // 初始化日历
        calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(item.getPurchaseDate());
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 设置物品数据到表单
        etName.setText(item.getName());
        etPrice.setText(String.valueOf(item.getPrice()));
        etPurchaseDate.setText(item.getPurchaseDate());
        if (item.getImageUri() != null) {
            selectedImageUri = item.getImageUri();
            ivItemImage.setImageURI(selectedImageUri);
        }

        // 设置购买日期选择器
        etPurchaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditItemActivity.this, dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // 设置图片选择
        ivItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        // 设置保存按钮
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivItemImage.setImageURI(selectedImageUri);
        }
    }

    private void saveChanges() {
        String name = etName.getText().toString();
        String priceStr = etPrice.getText().toString();
        String date = etPurchaseDate.getText().toString();

        if (name.isEmpty() || priceStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);

            // 更新物品对象
            item.setName(name);
            item.setPrice(price);
            item.setPurchaseDate(date);
            if (selectedImageUri != null) {
                item.setImageUri(selectedImageUri);
            }

            // 保存到数据库
            int rowsAffected = dbHelper.updateItem(item);

            if (rowsAffected > 0) {
                Toast.makeText(this, "物品已成功更新", Toast.LENGTH_SHORT).show();
                finish(); // 返回详情界面
            } else {
                Toast.makeText(this, "更新物品失败", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入有效的价格", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 返回上一界面
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
