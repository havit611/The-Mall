package com.themall.orderservice.client;

import com.themall.itemservice.entity.Inventory;
import com.themall.itemservice.entity.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// queries product information,
// checks inventory,
// deducts stock to ensure ordered items have sufficient inventory
@FeignClient(name = "item-service", url = "http://localhost:8082")
public interface ItemServiceClient {


    @GetMapping("/api/items/{itemId}")
    Item getItemById(@PathVariable("itemId") String itemId);
    
    @GetMapping("/api/inventory/{itemId}")
    Inventory getInventory(@PathVariable("itemId") String itemId);
    
    @PutMapping("/api/inventory/{itemId}")
    Inventory updateInventory(@PathVariable("itemId") String itemId, @RequestBody Inventory units);
    
    // DTOs for Item Service response
    record Item(String itemId, String upc, String itemName, 
                java.math.BigDecimal unitPrice, java.util.List<String> pictureUrls, 
                java.util.Map<String, Object> metadata) {}
    
    record Inventory(String id, String itemId, Integer availableUnits) {}
}