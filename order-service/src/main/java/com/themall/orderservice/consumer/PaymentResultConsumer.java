package com.themall.orderservice.consumer;

import com.themall.orderservice.client.ItemServiceClient;
import com.themall.orderservice.entity.Order;
import com.themall.orderservice.repository.OrderItemRepository;
import com.themall.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ItemServiceClient itemServiceClient;

    /**
     * 监听支付结果事件，更新订单状态
     */
    @KafkaListener(topics = "payment-result-events", groupId = "order-service-group")
    public void handlePaymentResult(String message) {
        try {
            log.info("Received payment result: {}", message);

            // 解析 JSON 消息
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            String orderId = (String) event.get("orderId");
            String paymentStatus = (String) event.get("status");
            String paymentId = (String) event.get("paymentId");

            // 更新订单状态
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

            switch (paymentStatus) {
                case "SUCCESS":
                    order.setStatus("PAID");
                    log.info("Order {} payment successful, payment ID: {}", orderId, paymentId);
                    break;
                case "FAILED":
                    order.setStatus("PAYMENT_FAILED");
                    log.warn("Order {} payment failed", orderId);
                    break;
                case "REFUNDED":
                    order.setStatus("REFUNDED");
                    log.info("Order {} refunded", orderId);
                    rollbackInventoryForFailedPayment(orderId);
                    break;
                default:
                    log.warn("Unknown payment status: {} for order: {}", paymentStatus, orderId);
            }

            orderRepository.save(order);

        } catch (Exception e) {
            log.error("Error processing payment result: {}", message, e);
        }
    }

    // TODO
    private void rollbackInventoryForFailedPayment(String orderId) {

    }
}