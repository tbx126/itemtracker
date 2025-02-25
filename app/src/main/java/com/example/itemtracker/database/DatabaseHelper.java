package com.example.itemtracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "item_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // 物品表
    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_PURCHASE_DATE = "purchase_date";
    public static final String COLUMN_IS_ACTIVE = "is_active";
    public static final String COLUMN_ICON = "icon";

    // 使用记录表
    public static final String TABLE_USAGE = "usage_records";
    public static final String COLUMN_USAGE_ID = "_id";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_USAGE_DATE = "usage_date";
    public static final String COLUMN_USAGE_NOTE = "note";

    // 创建表SQL语句
    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLE_ITEMS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_PRICE + " REAL NOT NULL, "
            + COLUMN_PURCHASE_DATE + " TEXT NOT NULL, "
            + COLUMN_IS_ACTIVE + " INTEGER NOT NULL, "
            + COLUMN_ICON + " BLOB);";

    private static final String CREATE_TABLE_USAGE = "CREATE TABLE " + TABLE_USAGE + "("
            + COLUMN_USAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_ITEM_ID + " INTEGER NOT NULL, "
            + COLUMN_USAGE_DATE + " TEXT NOT NULL, "
            + COLUMN_USAGE_NOTE + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_ITEM_ID + ") REFERENCES " + TABLE_ITEMS + "(" + COLUMN_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEMS);
        db.execSQL(CREATE_TABLE_USAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }
}
