package com.example.itemtracker.models;

import android.net.Uri;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Item {
    private long id;
    private String name;
    private double price;
    private String purchaseDate;
    private boolean isActive;
    private String imageUri;

    // 无参构造函数
    public Item() {
        this.isActive = true; // 默认为激活状态
    }

    // 基本构造函数
    public Item(String name, double price, String purchaseDate) {
        this.name = name;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.isActive = true; // 默认为激活状态
    }

    // 完整构造函数
    public Item(long id, String name, double price, String purchaseDate, boolean isActive, String imageUri) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.isActive = isActive;
        this.imageUri = imageUri;
    }

    // ID
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // 名称
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // 价格
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // 购买日期
    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    // 激活状态
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // 图片URI
    public Uri getImageUri() {
        if (imageUri != null && !imageUri.isEmpty()) {
            try {
                return Uri.parse(imageUri);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public void setImageUri(Uri uri) {
        if (uri != null) {
            this.imageUri = uri.toString();
        } else {
            this.imageUri = null;
        }
    }

    public void setImageUri(String uriString) {
        this.imageUri = uriString;
    }

    public String getImageUriString() {
        return imageUri;
    }

    // 已拥有天数
    public int getDaysOwned() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date purchaseDate = sdf.parse(this.purchaseDate);
            Date currentDate = new Date();
            long diffInMillis = currentDate.getTime() - purchaseDate.getTime();
            return (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            return 0;
        }
    }

    // 每日成本
    public double getCostPerDay() {
        int daysOwned = getDaysOwned();
        if (daysOwned == 0) return price;
        return price / daysOwned;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", isActive=" + isActive +
                ", imageUri='" + imageUri + '\'' +
                '}';
    }
}
