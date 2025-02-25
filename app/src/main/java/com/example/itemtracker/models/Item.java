package com.example.itemtracker.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Item implements Parcelable {

    private long id;
    private String name;
    private double price;
    private String purchaseDate; // 存储为字符串格式的日期
    private String imageUriString;
    private String note;
    private boolean isActive;
    private String category; // 添加分类字段

    // 默认日期格式
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    // 默认构造函数
    public Item() {
        this.isActive = true;
    }

    // 完整构造函数
    public Item(long id, String name, double price, String purchaseDate,
                String imageUriString, String note, boolean isActive) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.imageUriString = imageUriString;
        this.note = note;
        this.isActive = isActive;
    }

    // 不带id的构造函数，用于创建新物品
    public Item(String name, double price, String purchaseDate) {
        this.name = name;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.isActive = true;
    }

    // Parcelable 实现
    protected Item(Parcel in) {
        id = in.readLong();
        name = in.readString();
        price = in.readDouble();
        purchaseDate = in.readString();
        imageUriString = in.readString();
        note = in.readString();
        isActive = in.readByte() != 0;
        category = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(purchaseDate);
        dest.writeString(imageUriString);
        dest.writeString(note);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeString(category);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    // 获取Date对象的方法
    public Date getPurchaseDateAsDate() {
        try {
            return purchaseDate != null ? DATE_FORMAT.parse(purchaseDate) : null;
        } catch (ParseException e) {
            return new Date(); // 解析失败返回当前日期
        }
    }

    // 设置Date对象的方法
    public void setPurchaseDate(Date date) {
        this.purchaseDate = date != null ? DATE_FORMAT.format(date) : null;
    }

    public String getImageUriString() {
        return imageUriString;
    }

    public void setImageUriString(String imageUriString) {
        this.imageUriString = imageUriString;
    }

    // Uri相关方法
    public Uri getImageUri() {
        return imageUriString != null ? Uri.parse(imageUriString) : null;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUriString = imageUri != null ? imageUri.toString() : null;
    }

    // 兼容现有代码
    public void setImageUri(String imageUriString) {
        this.imageUriString = imageUriString;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // 兼容性方法
    public String getNotes() {
        return note;
    }

    public void setNotes(String notes) {
        this.note = notes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id &&
                Double.compare(item.price, price) == 0 &&
                isActive == item.isActive &&
                Objects.equals(name, item.name) &&
                Objects.equals(purchaseDate, item.purchaseDate) &&
                Objects.equals(imageUriString, item.imageUriString) &&
                Objects.equals(note, item.note) &&
                Objects.equals(category, item.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, purchaseDate, imageUriString, note, category, isActive);
    }

    @NonNull
    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", imageUriString='" + imageUriString + '\'' +
                ", note='" + note + '\'' +
                ", category='" + category + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    // 计算拥有天数的实用方法
    public int getDaysOwned() {
        try {
            Date purchaseDate = getPurchaseDateAsDate();
            if (purchaseDate == null) return 0;

            Date currentDate = new Date();
            long diffInMillies = Math.abs(currentDate.getTime() - purchaseDate.getTime());
            long diffInDays = diffInMillies / (24 * 60 * 60 * 1000);

            return (int) diffInDays;
        } catch (Exception e) {
            return 0;
        }
    }

    // 计算日均价格
    public double getDailyPrice() {
        int daysOwned = getDaysOwned();
        if (daysOwned > 0) {
            return price / daysOwned;
        }
        return price;
    }
}
