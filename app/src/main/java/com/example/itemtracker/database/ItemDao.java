// 文件路径: com/example/itemtracker/database/ItemDao.java
package com.example.itemtracker.database;

import com.example.itemtracker.models.Item;
import java.util.List;

public interface ItemDao {
    Item getItemById(long id);
    void update(Item item);
    void delete(Item item);
    void insert(Item item);
    List<Item> getAllItems(); // 添加这个方法
}
