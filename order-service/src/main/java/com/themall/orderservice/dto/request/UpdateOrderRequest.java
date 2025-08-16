package com.themall.orderservice.dto.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class UpdateOrderRequest {
    
    private String status;
    
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}