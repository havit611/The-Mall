package com.themall.itemservice.service.impl;

import com.themall.itemservice.entity.Inventory;
import com.themall.itemservice.repository.InventoryRepository;
import com.themall.itemservice.repository.ItemRepository;
import com.themall.itemservice.service.InventoryService;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;
    
    public InventoryServiceImpl(InventoryRepository inventoryRepository, ItemRepository itemRepository) {
        this.inventoryRepository = inventoryRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Inventory getInventory(String itemId) {
        // Precondition Validation
        if (!itemRepository.existsByUpc(itemId)) {
            throw new RuntimeException("Item not found");
        }
        return inventoryRepository.findByItemId(itemId)
            .orElseThrow(() -> new RuntimeException("Inventory not found"));
    }

    // TODO: use optimistic locking to preventing concurrent overselling issues
    @Override
    public Inventory updateInventory(String itemId, Integer units) {
        // Precondition Validation
        if (!itemRepository.existsByUpc(itemId)) {
            throw new RuntimeException("Item not found");
        }

        Inventory inventory = inventoryRepository.findByItemId(itemId)
            .orElse(createDefaultInventory(itemId));

        inventory.setAvailableUnits(units);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Integer getAvailableUnits(String itemId) {
        return inventoryRepository.findByItemId(itemId)
            .map(Inventory::getAvailableUnits)   // exist
            .orElse(0);                    // not exist
    }
    
    private Inventory createDefaultInventory(String itemId) {
        Inventory inventory = new Inventory();
        inventory.setItemId(itemId);
        inventory.setAvailableUnits(0);
        return inventory;
    }
}