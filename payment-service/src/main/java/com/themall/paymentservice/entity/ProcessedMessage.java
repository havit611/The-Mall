package com.themall.paymentservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_messages")
public class ProcessedMessage {

    @Id
    @Column(name = "message_id")
    private String messageId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "action")
    private String action;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

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