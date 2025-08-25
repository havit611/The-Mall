package com.themall.paymentservice.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

@Table("processed_messages")
public class ProcessedMessage {

    @PrimaryKey(value = "message_id", forceQuote = true)
    private String messageId;

    @Column(value = "order_id", forceQuote = true)
    private String orderId;

    @Column(value = "action", forceQuote = true)
    private String action;

    @Column(value = "processed_at", forceQuote = true)
    private LocalDateTime processedAt;

    // Constructors
    public ProcessedMessage() {
        this.processedAt = LocalDateTime.now();
    }

    public ProcessedMessage(String messageId, String orderId, String action) {
        this.messageId = messageId;
        this.orderId = orderId;
        this.action = action;
        this.processedAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}