package com.themall.itemservice.controller;

import com.themall.itemservice.dto.ItemRequest;
import com.themall.itemservice.entity.Item;
import com.themall.itemservice.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    
    private final ItemService itemService;
    
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // 创建新商品并初始化库存为0
    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody ItemRequest request) {
        Item item = itemService.createItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item); // 返回201
    }

//    @GetMapping("/{itemId}")
//    public Item getItemById(@PathVariable String itemId) {
//        return itemService.getItemById(itemId);
//    }
    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable("itemId") String itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping("/upc/{upc}")
    public Item getItemByUpc(@PathVariable("upc") String upc) {
        return itemService.getItemByUpc(upc);
    }

    @PutMapping("/{itemId}")
    public Item updateItem(@PathVariable("itemId") String itemId, @Valid @RequestBody ItemRequest request) {
        return itemService.updateItem(itemId, request);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") String itemId) {
        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
//    public List<Item> getAllItems() {
//        return itemService.getAllItems();
//    }

    // Update：change to pagination: avoid querying and returning all product data at once.
    // Below is the default paging format,
    // BUT the GET request can be specific like: GET /api/items?page=1&size=20&sortBy=itemName&sortDir=desc -- 第1页、每页20条、按商品名称降序排列
    public Page<Item> getAllItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "itemId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        return itemService.getAllItems(page, size, sortBy, sortDir);

    }
}