package com.themall.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "item-service", url = "http://localhost:8082")
public interface ItemServiceClient {


    @GetMapping("/api/items/{itemId}")
    Item getItemById(@PathVariable String itemId);
    
    @GetMapping("/api/inventory/{itemId}")
    Inventory getInventory(@PathVariable String itemId);
    
    @PutMapping("/api/inventory/{itemId}")
    Inventory updateInventory(@PathVariable String itemId, @RequestBody Integer units);
    
    // DTOs for Item Service response
    record Item(String itemId, String upc, String itemName, 
                java.math.BigDecimal unitPrice, java.util.List<String> pictureUrls, 
                java.util.Map<String, Object> metadata) {}
    
    record Inventory(String id, String itemId, Integer availableUnits) {}
}