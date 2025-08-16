package com.themall.paymentservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Schema(description = "支付信息")
@Entity
@Table(name = "payments")
public class Payment {
    
    @Schema(description = "支付ID", example = "pay-12345")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private String paymentId;
    
    @Schema(description = "关联的订单ID", example = "order-12345")
    @Column(name = "order_id", nullable = false)
    private String orderId;
    
    @Schema(description = "支付金额", example = "99.99")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Schema(description = "支付状态", example = "SUCCESS")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;
    
    @Schema(description = "幂等性键", example = "idempotency-12345")
    @Column(name = "idempotency_key", unique = true, nullable = false)
    private String idempotencyKey;
    
    public Payment() {}
    
    public Payment(String paymentId, String orderId, BigDecimal amount, PaymentStatus status, String idempotencyKey) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
    }

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

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
    
    @Schema(description = "支付状态枚举")
    public enum PaymentStatus {
        @Schema(description = "支付成功")
        SUCCESS, 
        @Schema(description = "支付失败")
        FAILED, 
        @Schema(description = "已退款")
        REFUNDED
    }
}