package com.themall.itemservice.service;

import com.themall.itemservice.entity.Inventory;

public interface InventoryService {
    Inventory getInventory(String itemId);
    Inventory updateInventory(String itemId, Integer units);
    Integer getAvailableUnits(String itemId);

}