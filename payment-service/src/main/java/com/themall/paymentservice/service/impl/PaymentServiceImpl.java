package com.themall.paymentservice.service.impl;

import com.themall.paymentservice.dto.PaymentRequest;
import com.themall.paymentservice.entity.Payment;
import com.themall.paymentservice.repository.PaymentRepository;
import com.themall.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    
    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentServiceImpl(PaymentRepository paymentRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // 创建支付订单，通过idempotencyKey确保同一请求不会重复创建支付记录；返回支付记录
    @Override
    public Payment submitPayment(PaymentRequest request, String idempotencyKey) {
        // 1. 记录调试日志，获得订单ID和idempotencyKey
        log.debug("Submitting payment for order: {}, idempotencyKey: {}", request.getOrderId(), idempotencyKey);

        // 2: 根据【idempotencyKey】查询数据库，检查是否已存在相同的支付记录
        return paymentRepository.findByIdempotencyKey(idempotencyKey)
            .orElseGet(() -> {
                // 3. 【数据库级别的去重】：只有当找不到相同的idempotencyKey时才创建新支付
                Payment payment = new Payment(
                    null, 
                    request.getOrderId(), 
                    request.getAmount(), 
                    Payment.PaymentStatus.SUCCESS, 
                    idempotencyKey
                );

                // 4. 将支付记录持久化到数据库
                Payment saved = paymentRepository.save(payment);
                log.info("Payment created successfully: {}", saved.getPaymentId());

                // 5. 构造Kafka消息
                String message = saved.getOrderId() + ":" + saved.getStatus() + ":" + System.currentTimeMillis();
                // 6. 发送支付事件到Kafka消息队列，通知其他微服务
                kafkaTemplate.send("payment-events", message);
                log.debug("Kafka message sent: {}", message);
                
                return saved;
            });
    }


    // 更新支付信息
    @Override
    public Payment updatePayment(String paymentId) {
        log.debug("Updating payment: {}", paymentId);
        
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }

    // 退款操作
    @Override
    public Payment reversePayment(String paymentId) {
        log.debug("Reversing payment: {}", paymentId);

        // 根据支付ID查支付信息
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        // 检查支付状态，避免重复退款
        if (payment.getStatus() != Payment.PaymentStatus.REFUNDED) {
            // 更新支付状态为已退款
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            // 持久化
            payment = paymentRepository.save(payment);
            log.info("Payment refunded successfully: {}", paymentId);

            // 构造退款事件消息
            String message = payment.getOrderId() + ":" + payment.getStatus() + ":" + System.currentTimeMillis();
            // 发送退款事件到Kafka，通知其他微服务
            kafkaTemplate.send("payment-events", message);
            log.debug("Kafka message sent: {}", message);
        }
        
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        log.debug("Getting payment: {}", paymentId);
        
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
    }
}