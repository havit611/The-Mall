package com.themall.orderservice.service.impl;

import com.themall.orderservice.dto.request.CreateOrderRequest;
import com.themall.orderservice.dto.request.UpdateOrderRequest;
import com.themall.orderservice.entity.Order;
import com.themall.orderservice.entity.OrderItem;
import com.themall.orderservice.repository.OrderItemRepository;
import com.themall.orderservice.repository.OrderRepository;
import com.themall.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, 
                           OrderItemRepository orderItemRepository,
                           KafkaTemplate<String, String> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // 创建订单方法：接收订单请求，持久化订单主表和明细表数据，并发送Kafka消息通知下游服务
    @Override
    public Order createOrder(CreateOrderRequest request) {
        // 1. 构建订单主表实体，设置初始状态为"CREATED"
        Order order = new Order(request.getUserId(), "CREATED", request.getTotalAmount());
        // 2. 将订单主表数据持久化到Cassandra数据库
        Order saved = orderRepository.save(order);

        // 3. 将请求中的订单项列表 --> 订单明细实体列表
        List<OrderItem> orderItems = request.getItems().stream()
            .map(item -> new OrderItem(
                saved.getOrderId(),
                item.getItemId(),
                item.getItemName(),
                item.getQuantity(),
                item.getUnitPrice()
            ))
            .collect(Collectors.toList());

        // 4. 保存所有订单明细, 到Cassandra数据库
        orderItemRepository.saveAll(orderItems);

        // 5. 发送订单创建事件到Kafka，通知其他微服务进行后续处理
        sendKafkaMessage(saved.getOrderId(), "CREATED");

        // 6. 返回创建成功的订单对象给调用方
        return saved;
    }

    // 取消订单：根据订单ID查找订单-->并更新状态为已取消-->同时发送取消事件
    @Override
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus("CANCELLED");
        orderRepository.save(order);

        // 发送订单取消事件到Kafka，通知下游服务处理取消逻辑
        sendKafkaMessage(orderId, "CANCELLED");
    }

    // 更新订单：根据请求内容,更新: 订单的状态 + 总金额
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

        // 将更新后的订单持久化到Cassandra数据库
        Order saved = orderRepository.save(order);
        // 发送订单更新事件到Kafka，携带最新的订单状态
        sendKafkaMessage(orderId, saved.getStatus());
        
        return saved;
    }

    // 查询订单：根据订单ID获取订单详情
    @Override
    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // 发送Kafka消息：构造订单事件消息并发送到Kafka消息队列
    private void sendKafkaMessage(String orderId, String status) {
        // 1. 构造消息
        String message = orderId + ":" + status + ":" + System.currentTimeMillis();
        // 2. 将消息异步发送到Kafka的"order-events" topic
        kafkaTemplate.send("order-events", message);
        // 3. 记录日志
        log.info("Sent Kafka message: {}", message);
    }
}