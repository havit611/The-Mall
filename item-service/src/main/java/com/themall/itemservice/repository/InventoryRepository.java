package com.themall.itemservice.repository;

import com.themall.itemservice.entity.Inventory;
import com.themall.itemservice.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Inherited Basic CRUD functionality
// Spring Data MongoDB automatically generates implementations by parsing method names
@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {     // use Spring Data MongoDB
    Optional<Inventory> findByItemId(String itemId);
    void deleteByItemId(String itemId);
    // 添加分页搜索方法
    Page<Item> findByItemNameContainingIgnoreCase(String itemName, Pageable pageable);
    Page<Item> findByUpcContaining(String upc, Pageable pageable);
}