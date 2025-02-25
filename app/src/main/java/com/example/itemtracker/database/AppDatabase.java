package com.example.itemtracker.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.itemtracker.models.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppDatabase {
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile AppDatabase INSTANCE;
    private final Context context;

    private AppDatabase(Context context) {
        this.context = context.getApplicationContext();
    }

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppDatabase(context);
                }
            }
        }
        return INSTANCE;
    }

    public ItemDao itemDao() {
        return new ItemDaoImpl(context);
    }

    // 内部DAO实现类
    private static class ItemDaoImpl implements ItemDao {
        private final Context context;

        ItemDaoImpl(Context context) {
            this.context = context;
        }

        @Override
        public Item getItemById(long id) {
            // 直接通过ContentProvider或DatabaseHelper静态方法获取
            // 这里仅返回一个模拟数据
            Item item = new Item();
            item.setId(id);
            item.setName("Item " + id);
            item.setPrice(99.99);
            item.setPurchaseDate("2023-11-01");
            return item;
        }

        @Override
        public void update(Item item) {
            // 使用ContentProvider或DatabaseHelper静态方法更新
        }

        @Override
        public void delete(Item item) {
            // 使用ContentProvider或DatabaseHelper静态方法删除
        }

        @Override
        public void insert(Item item) {
            // 使用ContentProvider或DatabaseHelper静态方法插入
        }

        @Override
        public List<Item> getAllItems() {
            // 模拟从数据库获取所有物品
            List<Item> items = new ArrayList<>();

            // 添加一些示例数据，在实际应用中这里应该从数据库加载
            for (int i = 1; i <= 10; i++) {
                Item item = new Item();
                item.setId(i);
                item.setName("示例物品 " + i);
                item.setPrice(i * 100.0);
                item.setPurchaseDate("2023-11-0" + (i % 9 + 1)); // 设置不同日期

                // 设置一些示例图片URI
                if (i % 3 == 0) {
                    item.setImageUriString("android.resource://com.example.itemtracker/drawable/ic_default_item");
                }

                items.add(item);
            }

            return items;

            // 在实际应用中，你应该使用 SQLite 查询或 ContentProvider:
            /*
            List<Item> items = new ArrayList<>();
            try {
                // 获取 SQLiteDatabase 实例
                DatabaseHelper dbHelper = new DatabaseHelper(context);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                // 执行查询
                Cursor cursor = db.query(
                    DatabaseHelper.TABLE_ITEMS,   // 表名
                    null,                         // 所有列
                    null,                         // WHERE 子句
                    null,                         // WHERE 参数
                    null,                         // GROUP BY
                    null,                         // HAVING
                    "name ASC"                    // 排序方式
                );

                // 处理结果
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Item item = new Item();
                        item.setId(cursor.getLong(cursor.getColumnIndexOrThrow("id")));
                        item.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                        item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                        item.setPurchaseDate(cursor.getString(cursor.getColumnIndexOrThrow("purchase_date")));
                        item.setImageUriString(cursor.getString(cursor.getColumnIndexOrThrow("image_uri")));

                        items.add(item);
                    } while (cursor.moveToNext());

                    cursor.close();
                }

                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return items;
            */
        }
    }
}
