package com.themall.orderservice.repository;

import com.themall.orderservice.entity.Order;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends CassandraRepository<Order, String> {
    @Query("SELECT column_name FROM system_schema.columns WHERE keyspace_name = ?0 AND table_name = ?1")
    List<Map<String, String>> desc(String keyspaceName, String tableName);
}