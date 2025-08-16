package com.themall.orderservice.repository;

import com.themall.orderservice.entity.OrderItem;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends CassandraRepository<OrderItem, OrderItem.OrderItemKey> {
    
    @Query("SELECT * FROM order_items WHERE order_id = ?0")
    List<OrderItem> findByOrderId(String orderId);
    
    @Query("DELETE FROM order_items WHERE order_id = ?0")
    void deleteByOrderId(String orderId);
}