package com.themall.itemservice.repository;

import com.themall.itemservice.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    Optional<Item> findByUpc(String upc);
    boolean existsByUpc(String upc);
    // 添加分页搜索方法
    Page<Item> findByItemNameContainingIgnoreCase(String itemName, Pageable pageable);
    Page<Item> findByUpcContaining(String upc, Pageable pageable);
}