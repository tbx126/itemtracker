// app/src/main/java/com/example/itemtracker/db/DatabaseHelper.java
package com.example.itemtracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.example.itemtracker.models.Item;
import com.example.itemtracker.models.UsageRecord;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "item_tracker.db";

    // 表名
    private static final String TABLE_ITEMS = "items";
    private static final String TABLE_USAGE = "usage_records";

    // 物品表列名
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PRICE = "price";
    private static final String KEY_PURCHASE_DATE = "purchase_date";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_IMAGE_URI = "image_uri";

    // 使用记录表列名
    private static final String KEY_USAGE_ID = "id";
    private static final String KEY_ITEM_ID = "item_id";
    private static final String KEY_USAGE_DATE = "date";
    private static final String KEY_USAGE_NOTE = "note";

    // 单例实例
    private static DatabaseHelper sInstance;

    // 获取单例实例
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建物品表
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_PRICE + " REAL,"
                + KEY_PURCHASE_DATE + " TEXT,"
                + KEY_ACTIVE + " INTEGER,"
                + KEY_IMAGE_URI + " TEXT"
                + ")";
        db.execSQL(CREATE_ITEMS_TABLE);

        // 创建使用记录表
        String CREATE_USAGE_TABLE = "CREATE TABLE " + TABLE_USAGE + "("
                + KEY_USAGE_ID + " INTEGER PRIMARY KEY,"
                + KEY_ITEM_ID + " INTEGER,"
                + KEY_USAGE_DATE + " TEXT,"
                + KEY_USAGE_NOTE + " TEXT,"
                + "FOREIGN KEY(" + KEY_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + KEY_ID + ")"
                + ")";
        db.execSQL(CREATE_USAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时删除旧表并重新创建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // 添加新物品
    public long addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_PRICE, item.getPrice());
        values.put(KEY_PURCHASE_DATE, item.getPurchaseDate());
        values.put(KEY_ACTIVE, item.isActive() ? 1 : 0);
        values.put(KEY_IMAGE_URI, item.getImageUri() != null ? item.getImageUri().toString() : null);

        // 插入行
        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return id;
    }

    // 获取单个物品
    public Item getItem(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Item item = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                item = cursorToItem(cursor);
            }
            cursor.close();
        }
        return item;
    }

    // 获取所有物品
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = cursorToItem(cursor);
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    // 更新物品
    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_PRICE, item.getPrice());
        values.put(KEY_PURCHASE_DATE, item.getPurchaseDate());
        values.put(KEY_ACTIVE, item.isActive() ? 1 : 0);
        values.put(KEY_IMAGE_URI, item.getImageUri() != null ? item.getImageUri().toString() : null);

        // 更新行
        return db.update(TABLE_ITEMS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // 删除物品
    public void deleteItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // 添加使用记录
    public long addUsageRecord(UsageRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_ID, record.getItemId());
        values.put(KEY_USAGE_DATE, record.getDate());
        values.put(KEY_USAGE_NOTE, record.getNote());

        // 插入行
        long id = db.insert(TABLE_USAGE, null, values);
        db.close();
        return id;
    }

    // 获取物品的所有使用记录
    public List<UsageRecord> getAllUsageRecords(long itemId) {
        List<UsageRecord> recordList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USAGE, null, KEY_ITEM_ID + "=?",
                new String[]{String.valueOf(itemId)}, null, null, KEY_USAGE_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                UsageRecord record = new UsageRecord(
                        cursor.getLong(cursor.getColumnIndexOrThrow(KEY_USAGE_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ITEM_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_USAGE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_USAGE_NOTE))
                );
                recordList.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recordList;
    }

    // 计算总资产值
    public double getTotalAssetValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + KEY_PRICE + ") FROM " + TABLE_ITEMS, null);
        double totalValue = 0;
        if (cursor.moveToFirst()) {
            totalValue = cursor.getDouble(0);
        }
        cursor.close();
        return totalValue;
    }

    // 获取物品总数
    public int getItemCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // 获取激活物品数量
    public int getActiveItemCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS + " WHERE " + KEY_ACTIVE + "=1", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // 计算日均成本（物品总价值除以使用天数）
    public double getAverageDailyCost() {
        // 简化实现，这里只用总价值除以30天
        return getTotalAssetValue() / 30;
    }

    // 从数据库游标读取物品对象
    private Item cursorToItem(Cursor cursor) {
        Item item = new Item();
        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
        item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PRICE)));
        item.setPurchaseDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_PURCHASE_DATE)));
        item.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ACTIVE)) == 1);

        String uriString = cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE_URI));
        if (uriString != null) {
            item.setImageUri(Uri.parse(uriString));
        }

        return item;
    }
}
