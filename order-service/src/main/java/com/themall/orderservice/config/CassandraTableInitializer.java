package com.themall.orderservice.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;

@Component
public class CassandraTableInitializer {

    private static final Logger log = LoggerFactory.getLogger(CassandraTableInitializer.class);

    @Autowired
    private CassandraTemplate cassandraTemplate;

    @PostConstruct
    public void createTables() {
        log.info("Initializing Cassandra tables...");

        try {
            // Create orders table
            cassandraTemplate.getCqlOperations().execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    order_id text PRIMARY KEY,
                    user_id text,
                    status text,
                    total_amount decimal,
                    created_at timestamp
                )
                """);
            log.info("Orders table ready");

            // Create order_items table
            cassandraTemplate.getCqlOperations().execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    order_id text,
                    item_id text,
                    item_name text,
                    quantity int,
                    unit_price decimal,
                    PRIMARY KEY (order_id, item_id)
                )
                """);
            log.info("Order_items table ready");

            // Create kafka_processed_messages table
            cassandraTemplate.getCqlOperations().execute("""
                CREATE TABLE IF NOT EXISTS kafka_processed_messages (
                    message_id text PRIMARY KEY,
                    order_id text,
                    status text,
                    timestamp bigint,
                    processed_at timestamp
                )
                """);
            log.info("Kafka_processed_messages table ready");

        } catch (Exception e) {
            log.error("Error creating tables: ", e);
            throw e;
        }
    }
}