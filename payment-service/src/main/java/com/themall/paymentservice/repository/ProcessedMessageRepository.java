package com.themall.paymentservice.repository;

import com.themall.paymentservice.entity.ProcessedMessage;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedMessageRepository extends CassandraRepository<ProcessedMessage, String> {

    // Cassandra 使用 CQL 查询
    @Query("SELECT COUNT(*) FROM processed_messages WHERE message_id = ?0")
    int existsByMessageId(String messageId);

//    // 使用 TTL 自动清理旧数据（30天后自动删除）
//    @Query("INSERT INTO processed_messages (message_id, order_id, action, processed_at) " +
//            "VALUES (?0, ?1, ?2, ?3) USING TTL 2592000")
//    void insertWithTTL(String messageId, String orderId, String action, LocalDateTime processedAt);
}