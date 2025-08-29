package com.themall.paymentservice.service.impl;

import com.themall.paymentservice.dto.PaymentRequest;
import com.themall.paymentservice.entity.Payment;
import com.themall.paymentservice.repository.PaymentRepository;
import com.themall.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentServiceImpl(PaymentRepository paymentRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Payment submitPayment(PaymentRequest request) {
        log.debug("Submitting payment for order: {}", request.getOrderId());

        return paymentRepository.findByOrderId(request.getOrderId())
                .orElseGet(() -> {
                    Payment payment = new Payment(request.getOrderId(), request.getAmount());
                    payment.setStatus(Payment.PaymentStatus.SUCCESS);

                    Payment saved = paymentRepository.save(payment);
                    log.info("Payment created successfully: {}", saved.getPaymentId());

                    return saved;
                });
    }

    @Override
    public Payment updatePayment(String paymentId) {
        log.debug("Updating payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        payment.setUpdatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    @Override
    public Payment reversePayment(String paymentId) {
        log.debug("Reversing payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        if (payment.getStatus() != Payment.PaymentStatus.REFUNDED) {
            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            payment.setRefundedAt(LocalDateTime.now());
            payment = paymentRepository.save(payment);
            log.info("Payment refunded successfully: {}", paymentId);

            String message = String.format("%s:%s:%d",
                    payment.getOrderId(),
                    payment.getStatus(),
                    System.currentTimeMillis()
            );
            kafkaTemplate.send("payment-result-events", payment.getOrderId(), message);
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