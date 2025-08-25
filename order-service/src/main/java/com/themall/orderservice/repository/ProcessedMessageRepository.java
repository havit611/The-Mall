package com.themall.orderservice.repository;

import com.themall.orderservice.entity.ProcessedMessage;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface ProcessedMessageRepository extends CassandraRepository<ProcessedMessage, String> {

    // 检查消息是否已存在
    @Query("SELECT COUNT(*) FROM kafka_processed_messages WHERE message_id = ?0")
    int existsByMessageId(String messageId);

    // 可选：自定义TTL插入（如果需要设置过期时间）
    @Query("INSERT INTO kafka_processed_messages (message_id, order_id, status, timestamp, processed_at) " +
            "VALUES (?0, ?1, ?2, ?3, toTimestamp(now())) USING TTL ?4")
    void insertWithTTL(String messageId, String orderId, String status, long timestamp, int ttlSeconds);
}
