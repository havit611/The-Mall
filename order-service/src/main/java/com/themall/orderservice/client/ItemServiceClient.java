package com.themall.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

// 删除这些错误的import
// import com.themall.itemservice.entity.Inventory;
// import com.themall.itemservice.entity.Item;

@FeignClient(name = "item-service", url = "http://localhost:8082")
public interface ItemServiceClient {

    @GetMapping("/api/items/{itemId}")
    Item getItemById(@PathVariable("itemId") String itemId);

    @GetMapping("/api/inventory/{itemId}")
    Inventory getInventory(@PathVariable("itemId") String itemId);

    @PutMapping("/api/inventory/{itemId}")
    Inventory updateInventory(@PathVariable("itemId") String itemId, @RequestBody Inventory units);

    // 在本地定义DTOs，不要引用其他服务的类
    record Item(String itemId, String upc, String itemName,
                java.math.BigDecimal unitPrice, java.util.List<String> pictureUrls,
                java.util.Map<String, Object> metadata) {}

    record Inventory(String id, String itemId, Integer availableUnits) {}
}