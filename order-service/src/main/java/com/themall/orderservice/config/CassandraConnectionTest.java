package com.themall.orderservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;
import com.datastax.oss.driver.api.core.CqlSession;

@Component
public class CassandraConnectionTest implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CassandraConnectionTest.class);

    @Autowired
    private CassandraTemplate cassandraTemplate;

    @Autowired
    private CqlSession cqlSession;

    @Override
    public void run(String... args) {
        log.info("========== CASSANDRA CONNECTION TEST ==========");

        try {
            // 1. 检查当前 keyspace
            String currentKeyspace = cqlSession.getKeyspace()
                    .map(ks -> ks.toString())
                    .orElse("NO KEYSPACE SET");
            log.info("Current Keyspace: {}", currentKeyspace);

            // 2. 尝试切换到 order_service
            log.info("Attempting to use order_service keyspace...");
            cassandraTemplate.getCqlOperations().execute("USE order_service");
            log.info("Successfully switched to order_service");

            // 3. 列出 order_service 中的所有表
            var tables = cassandraTemplate.getCqlOperations()
                    .queryForList("SELECT table_name FROM system_schema.tables WHERE keyspace_name = 'order_service'");
            log.info("Tables in order_service: {}", tables);

            // 4. 测试 orders 表查询
            log.info("Testing orders table...");
            cassandraTemplate.getCqlOperations().execute("SELECT * FROM orders LIMIT 1");
            log.info("✓ Orders table is accessible");

            // 5. 测试 order_items 表查询
            log.info("Testing order_items table...");
            cassandraTemplate.getCqlOperations().execute("SELECT * FROM order_items LIMIT 1");
            log.info("✓ Order_items table is accessible");

            // 6. 测试 kafka_processed_messages 表查询
            log.info("Testing kafka_processed_messages table...");
            cassandraTemplate.getCqlOperations().execute("SELECT * FROM kafka_processed_messages LIMIT 1");
            log.info("✓ Kafka_processed_messages table is accessible");

            log.info("========== ALL TABLES ACCESSIBLE ==========");

        } catch (Exception e) {
            log.error("Connection test failed: {}", e.getMessage());
            log.error("Full error: ", e);

            // 尝试获取更多诊断信息
            try {
                log.info("Trying to list all keyspaces...");
                var keyspaces = cassandraTemplate.getCqlOperations()
                        .queryForList("SELECT keyspace_name FROM system_schema.keyspaces");
                log.info("Available keyspaces: {}", keyspaces);
            } catch (Exception ex) {
                log.error("Failed to list keyspaces: {}", ex.getMessage());
            }
        }
    }
}