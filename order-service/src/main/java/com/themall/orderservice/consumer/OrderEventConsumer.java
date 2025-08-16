package com.themall.orderservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// OrderEventConsumer 订单事件消费者：监听并处理来自Kafka的订单事件消息
// 消费端 -- 异步接收和处理订单状态变更事件
@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    /**
     * 处理订单事件：消费订单事件消息, 并根据事件类型执行相应处理
     * 监听配置：主题="order-events", 消费者组="order-service-group"
     * @param message 从Kafka接收的原始字符串消息
     */
    @KafkaListener(topics = "order-events", groupId = "order-service-group")
    public void handleOrderEvent(String message) {
        // try-catch:消费者消费失败后不会不断重试，不会卡在这条坏消息上
        try {
            // 1. 记录接收到的原始消息
            log.info("Received Kafka message: {}", message);

            // 2. 解析消息内容
            String[] parts = message.split(":");
            // 3. 验证消息格式
            if (parts.length == 3) {
                // 4. 提取信息
                String orderId = parts[0];
                String status = parts[1];
                String timestamp = parts[2];

                // 5. 根据订单状态 ～ 执行对应的业务处理逻辑
                switch (status) {
                    // 5.1 处理订单创建事件
                    case "CREATED":
                        log.info("Order created - ID: {}, Timestamp: {}", orderId, timestamp);
                        // TODO: 调用库存服务预留库存
                        // TODO: 初始化支付流程
                        // TODO: 发送订单确认通知
                        break;
                    // 5.2 处理订单更新事件
                    case "UPDATED":
                        log.info("Order updated - ID: {}, Timestamp: {}", orderId, timestamp);
                        break;
                    // 53. 取消事件
                    case "CANCELLED":
                        log.warn("Order cancelled - ID: {}, Timestamp: {}", orderId, timestamp);
                        break;
                    // 5.4 兜底处理：其他状态变更
                    default:
                        log.info("Order status changed - ID: {}, Status: {}, Timestamp: {}", orderId, status, timestamp);
                        break;
                }
            } else {
                log.warn("Invalid message format received: {}", message);
            }
        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", message, e);
        }
    }
}