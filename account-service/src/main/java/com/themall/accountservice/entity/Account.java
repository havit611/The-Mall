package com.themall.accountservice.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    private String accountId;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String username;
    private String password;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    
    public Account() {}
    
    public Account(String email, String username, String password, String shippingAddress, String billingAddress, String paymentMethod) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod;
    }
    
    @PrePersist
    public void generateId() {
        if (accountId == null) {
            accountId = UUID.randomUUID().toString();
        }
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}