package com.themall.paymentservice.service;

import com.themall.paymentservice.dto.PaymentRequest;
import com.themall.paymentservice.entity.Payment;

public interface PaymentService {
    
    Payment submitPayment(PaymentRequest request);
    
    Payment updatePayment(String paymentId);
    
    Payment reversePayment(String paymentId);
    
    Payment getPayment(String paymentId);
}