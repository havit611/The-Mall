package com.themall.paymentservice.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("payments")
public class Payment {

    @PrimaryKey(value = "payment_id", forceQuote = true)
    private String paymentId;

    @Column(value = "order_id", forceQuote = true)
    private String orderId;

    @Column(value = "amount", forceQuote = true)
    private BigDecimal amount;

    @Column(value = "status", forceQuote = true)
    private PaymentStatus status;

    @Column(value = "created_at", forceQuote = true)
    private LocalDateTime createdAt;

    @Column(value = "updated_at", forceQuote = true)
    private LocalDateTime updatedAt;

    @Column(value = "refunded_at", forceQuote = true)
    private LocalDateTime refundedAt;

    public enum PaymentStatus {
        PENDING,
        SUCCESS,
        FAILED,
        UPDATED,
        REFUNDED
    }

    // Constructor
    public Payment(Object o, String orderId, BigDecimal amount, PaymentStatus success, String s) {
        this.paymentId = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public Payment(String orderId, BigDecimal amount) {
        this(null, request.getOrderId(), request.getAmount(), PaymentStatus.SUCCESS, String.valueOf(System.currentTimeMillis()));
        this.orderId = orderId;
        this.amount = amount;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }
}