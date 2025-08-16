package com.themall.orderservice.controller;

import com.themall.orderservice.dto.request.CreateOrderRequest;
import com.themall.orderservice.dto.request.UpdateOrderRequest;
import com.themall.orderservice.entity.Order;
import com.themall.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 接收和处理所有订单相关的HTTP请求
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order); // CREATED：201
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build(); // 204
    }

    // 先验证@Valid：如果用户提供了totalAmount字段，其值必须为正数(见UpdateOrderRequest) ；然后更新数据，并返回
    @PutMapping("/{orderId}")
    public Order updateOrder(@PathVariable String orderId, @Valid @RequestBody UpdateOrderRequest request) {
        return orderService.updateOrder(orderId, request);
    }


    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        return orderService.getOrder(orderId);
    }
}