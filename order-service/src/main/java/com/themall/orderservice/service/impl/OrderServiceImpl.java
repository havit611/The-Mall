package com.themall.orderservice.service.impl;

import com.themall.orderservice.dto.request.CreateOrderRequest;
import com.themall.orderservice.dto.request.UpdateOrderRequest;
import com.themall.orderservice.entity.Order;
import com.themall.orderservice.entity.OrderItem;
import com.themall.orderservice.repository.OrderItemRepository;
import com.themall.orderservice.repository.OrderRepository;
import com.themall.orderservice.service.OrderService;
import com.themall.orderservice.client.ItemServiceClient;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ItemServiceClient itemServiceClient;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            KafkaTemplate<String, String> kafkaTemplate,
                            ItemServiceClient itemServiceClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.itemServiceClient = itemServiceClient;
    }

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // 1. 创建并保存订单
        Order order = new Order(request.getUserId(), "PENDING", request.getTotalAmount());
        Order saved = orderRepository.save(order);

        // 2. 保存订单detail
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> new OrderItem(
                        saved.getOrderId(),
                        item.getItemId(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .collect(Collectors.toList());
        orderItemRepository.saveAll(orderItems);

        try {
            // 3. 直接处理库存检查和扣减（同步）
            boolean inventorySuccess = processInventory(saved.getOrderId(), orderItems);

            if (!inventorySuccess) {
                saved.setStatus("FAILED_INSUFFICIENT_INVENTORY");
                orderRepository.save(saved);
                throw new RuntimeException("Insufficient inventory for order: " + saved.getOrderId());
            }

            // 4. 库存处理成功，更新订单状态
            saved.setStatus("INVENTORY_RESERVED");
            orderRepository.save(saved);

            // 5. 发送支付请求事件（异步）
            sendPaymentRequest(saved);

            // 6. 发布订单创建事件（供其他服务监听，如通知、分析等）
            publishOrderEvent(saved, "ORDER_CREATED");

        } catch (Exception e) {
            log.error("Error creating order: {}", saved.getOrderId(), e);
            saved.setStatus("FAILED");
            orderRepository.save(saved);

            // 如果已经扣减了库存，需要回滚
            rollbackInventory(saved.getOrderId(), orderItems);
            throw e;
        }

        return saved;
    }

    @Override
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 更新订单状态
        order.setStatus("CANCELLED");
        orderRepository.save(order);

        // 发送取消支付请求
        sendPaymentCancellation(order);

        // 发布订单取消事件
        publishOrderEvent(order, "ORDER_CANCELLED");

        // 恢复库存
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        rollbackInventory(orderId, orderItems);
    }

    @Override
    public Order updateOrder(String orderId, UpdateOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
        }
        if (request.getTotalAmount() != null) {
            order.setTotalAmount(request.getTotalAmount());
        }

        Order saved = orderRepository.save(order);

        // 发布订单更新事件
        publishOrderEvent(saved, "ORDER_UPDATED");

        // 如果金额变化，发送支付更新请求
        if (request.getTotalAmount() != null) {
            sendPaymentUpdate(saved);
        }

        return saved;
    }

    @Override
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }


    // processInventory Synchronously
    private boolean processInventory(String orderId, List<OrderItem> orderItems) {
        try {
            for (OrderItem orderItem : orderItems) {
                ItemServiceClient.Inventory inventory =
                        itemServiceClient.getInventory(orderItem.getKey().getItemId());

                if (inventory.availableUnits() < orderItem.getQuantity()) {
                    log.warn("Insufficient inventory for item: {}", orderItem.getKey().getItemId());
                    return false;
                }

                // 扣减库存
                ItemServiceClient.Inventory updated = new ItemServiceClient.Inventory(
                        inventory.id(),
                        inventory.itemId(),
                        inventory.availableUnits() - orderItem.getQuantity()
                );
                itemServiceClient.updateInventory(inventory.itemId(), updated);
            }
            return true;
        } catch (Exception e) {
            log.error("Error processing inventory for order: {}", orderId, e);
            return false;
        }
    }

    // rollback Inventory
    private void rollbackInventory(String orderId, List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            try {
                ItemServiceClient.Inventory inventory =
                        itemServiceClient.getInventory(orderItem.getKey().getItemId());

                ItemServiceClient.Inventory updated = new ItemServiceClient.Inventory(
                        inventory.id(),
                        inventory.itemId(),
                        inventory.availableUnits() + orderItem.getQuantity()
                );
                itemServiceClient.updateInventory(inventory.itemId(), updated);
            } catch (Exception e) {
                log.error("Error rolling back inventory for item: {}",
                        orderItem.getKey().getItemId(), e);
            }
        }
    }

    // 发送支付请求
    private void sendPaymentRequest(Order order) {
        String messageId = UUID.randomUUID().toString();
        String message = String.format(
                "{\"messageId\":\"%s\",\"orderId\":\"%s\",\"amount\":%s,\"action\":\"CREATE_PAYMENT\",\"timestamp\":%d}",
                messageId, order.getOrderId(), order.getTotalAmount(), System.currentTimeMillis()
        );

        kafkaTemplate.send("payment-request-events", order.getOrderId(), message);
        log.info("Sent payment request for order: {}", order.getOrderId());
    }

    // 发送支付取消请求
    private void sendPaymentCancellation(Order order) {
        String messageId = UUID.randomUUID().toString();
        String message = String.format(
                "{\"messageId\":\"%s\",\"orderId\":\"%s\",\"action\":\"CANCEL_PAYMENT\",\"timestamp\":%d}",
                messageId, order.getOrderId(), System.currentTimeMillis()
        );

        kafkaTemplate.send("payment-request-events", order.getOrderId(), message);
        log.info("Sent payment cancellation for order: {}", order.getOrderId());
    }

    // 发送支付更新请求
    private void sendPaymentUpdate(Order order) {
        String messageId = UUID.randomUUID().toString();
        String message = String.format(
                "{\"messageId\":\"%s\",\"orderId\":\"%s\",\"amount\":%s,\"action\":\"UPDATE_PAYMENT\",\"timestamp\":%d}",
                messageId, order.getOrderId(), order.getTotalAmount(), System.currentTimeMillis()
        );

        kafkaTemplate.send("payment-request-events", order.getOrderId(), message);
        log.info("Sent payment update for order: {}", order.getOrderId());
    }

    // 发布订单领域事件（供其他服务监听）
    private void publishOrderEvent(Order order, String eventType) {
        String message = String.format(
                "{\"orderId\":\"%s\",\"userId\":\"%s\",\"status\":\"%s\",\"amount\":%s,\"eventType\":\"%s\",\"timestamp\":%d}",
                order.getOrderId(), order.getUserId(), order.getStatus(),
                order.getTotalAmount(), eventType, System.currentTimeMillis()
        );

        kafkaTemplate.send("order-domain-events", order.getOrderId(), message);
        log.info("Published {} event for order: {}", eventType, order.getOrderId());
    }
}