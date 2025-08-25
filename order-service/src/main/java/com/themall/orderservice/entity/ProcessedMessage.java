package com.themall.orderservice.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table("kafka_processed_messages")
public class ProcessedMessage {
    @PrimaryKey(value = "message_id", forceQuote = true)
    private String messageId;  // 使用消息中的UUID作为主键

    @Column(value = "order_id", forceQuote = true)
    private String orderId;
    @Column(value = "status", forceQuote = true)
    private String status;
    @Column(value = "timestamp", forceQuote = true)
    private long timestamp;
    @Column(value = "processed_at", forceQuote = true)
    private LocalDateTime processedAt;

    // 必须有无参构造函数（Cassandra要求）
    public ProcessedMessage() {
        this.processedAt = LocalDateTime.now();
    }

    public ProcessedMessage(String messageId, String orderId, String status, long timestamp) {
        this.messageId = messageId;
        this.orderId = orderId;
        this.status = status;
        this.timestamp = timestamp;
        this.processedAt = LocalDateTime.now();
    }

    // getters and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}

