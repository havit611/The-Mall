package com.themall.itemservice.service;

import com.themall.itemservice.dto.ItemRequest;
import com.themall.itemservice.entity.Item;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {
    Item createItem(ItemRequest request);
    Item getItemById(String itemId);
    Item getItemByUpc(String upc);
    Item updateItem(String itemId, ItemRequest request);
    void deleteItem(String itemId);
    // List<Item> getAllItems();
    Page<Item> getAllItems(int page, int size, String sortBy, String sortDir);
}