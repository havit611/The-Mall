package com.themall.itemservice.controller;

import com.themall.itemservice.entity.Inventory;
import com.themall.itemservice.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // 获取指定商品的完整库存信息
    @GetMapping("/{itemId}")
    public Inventory getInventory(@PathVariable("itemId") String itemId) {
        return inventoryService.getInventory(itemId);
    }


    // 仅获取指定商品的可用库存数量 -- for 购物车页面快速检查是否有货时用
    @GetMapping("/{itemId}/available")
    public Integer getAvailableUnits(@PathVariable("itemId") String itemId) {
        return inventoryService.getAvailableUnits(itemId);
    }

    // 更新指定商品的库存数量为新值 -- for 盘点库存或手动调整库存数量时用
    @PutMapping("/{itemId}")
    public Inventory updateInventory(@PathVariable("itemId") String itemId, @RequestBody Inventory units) {
        return inventoryService.updateInventory(itemId, units.getAvailableUnits());
    }
}