package com.themall.paymentservice.consumer;

import com.themall.paymentservice.dto.PaymentRequest;
import com.themall.paymentservice.entity.Payment;
import com.themall.paymentservice.entity.ProcessedMessage;
import com.themall.paymentservice.repository.PaymentRepository;
import com.themall.paymentservice.repository.ProcessedMessageRepository;
import com.themall.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
public class PaymentRequestConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentRequestConsumer.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProcessedMessageRepository processedMessageRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "payment-request-events", groupId = "payment-service-group")
    public void handlePaymentRequest(String message) {
        try {
            log.info("Received payment request: {}", message);

            // Parse the JSON message
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            String messageId = (String) event.get("messageId");
            String orderId = (String) event.get("orderId");
            String action = (String) event.get("action");

            // *****idempotency check******
            if (processedMessageRepository.existsByMessageId(messageId) > 0) {
                log.warn("Duplicate message detected, messageId: {}", messageId);

                // duplicate CREATE_PAYMENT requests: return the existing payment result
                if ("CREATE_PAYMENT".equals(action)) {
                    Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
                    if (existingPayment.isPresent()) {
                        sendPaymentResult(existingPayment.get());
                    }
                }
                return;
            }

            // process the request according to the specified action
            switch (action) {
                case "CREATE_PAYMENT":
                    handleCreatePayment(orderId, new BigDecimal(event.get("amount").toString()));
                    break;
                case "UPDATE_PAYMENT":
                    handleUpdatePayment(orderId, new BigDecimal(event.get("amount").toString()));
                    break;
                case "CANCEL_PAYMENT":
                    handleCancelPayment(orderId);
                    break;
                default:
                    log.warn("Unknown action: {} for order: {}", action, orderId);
            }

            // 保存处理记录
            ProcessedMessage processedMessage = new ProcessedMessage(messageId, orderId, action);
            processedMessageRepository.save(processedMessage);

        } catch (Exception e) {
            log.error("Error processing payment request: {}", message, e);
        }
    }

    private void handleCreatePayment(String orderId, BigDecimal amount) {
        log.info("Creating payment for order: {}, amount: {}", orderId, amount);

        // 检查是否已存在支付记录
        Optional<Payment> existing = paymentRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            log.warn("Payment already exists for order: {}", orderId);
            sendPaymentResult(existing.get());
            return;
        }

        // 创建支付请求
        PaymentRequest request = new PaymentRequest();
        request.setOrderId(orderId);
        request.setAmount(amount);

        // 处理支付
        Payment payment = paymentService.submitPayment(request);

        // 发送支付结果
        sendPaymentResult(payment);
    }

    private void handleUpdatePayment(String orderId, BigDecimal newAmount) {
        log.info("Updating payment for order: {}, new amount: {}", orderId, newAmount);

        Optional<Payment> existing = paymentRepository.findByOrderId(orderId);
        if (!existing.isPresent()) {
            log.error("Payment not found for order: {}", orderId);
            return;
        }

        Payment payment = existing.get();
        payment.setAmount(newAmount);
        payment.setStatus(Payment.PaymentStatus.UPDATED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updated = paymentRepository.save(payment);
        sendPaymentResult(updated);
    }

    private void handleCancelPayment(String orderId) {
        log.info("Cancelling payment for order: {}", orderId);

        Optional<Payment> existing = paymentRepository.findByOrderId(orderId);
        if (!existing.isPresent()) {
            log.warn("Payment not found for order: {}", orderId);
            return;
        }

        Payment payment = existing.get();
        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setAmount(BigDecimal.ZERO);
        payment.setRefundedAt(LocalDateTime.now());

        Payment refunded = paymentRepository.save(payment);
        sendPaymentResult(refunded);
    }

    private void sendPaymentResult(Payment payment) {
        try {
            String resultMessage = String.format(
                    "{\"orderId\":\"%s\",\"paymentId\":\"%s\",\"status\":\"%s\",\"amount\":%s,\"timestamp\":%d}",
                    payment.getOrderId(),
                    payment.getPaymentId(),
                    payment.getStatus().toString(),
                    payment.getAmount(),
                    System.currentTimeMillis()
            );

            kafkaTemplate.send("payment-result-events", payment.getOrderId(), resultMessage);
            log.info("Sent payment result for order: {}, status: {}",
                    payment.getOrderId(), payment.getStatus());

        } catch (Exception e) {
            log.error("Error sending payment result for order: {}", payment.getOrderId(), e);
        }
    }
}