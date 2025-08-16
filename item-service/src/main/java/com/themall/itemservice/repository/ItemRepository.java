package com.themall.itemservice.repository;

import com.themall.itemservice.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends MongoRepository<Item, String> {
    Optional<Item> findByUpc(String upc);
    boolean existsByUpc(String upc);
}