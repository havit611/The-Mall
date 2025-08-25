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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
        // 从 Security Context 获取当前用户
        String authenticatedUserId = getCurrentUserId();

        // validate token first
        if (!authenticatedUserId.equals(request.getUserId())) {
            throw new RuntimeException("User ID mismatch - you can only create orders for yourself");
        }

        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        String authenticatedUserId = getCurrentUserId();

        Order order = orderService.getOrder(orderId);
        if (!authenticatedUserId.equals(order.getUserId())) {
            throw new RuntimeException("Access denied - you can only view your own orders");
        }

        return order;
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable("orderId") String orderId) {
        String authenticatedUserId = getCurrentUserId();

        Order order = orderService.getOrder(orderId);
        if (!authenticatedUserId.equals(order.getUserId())) {
            throw new RuntimeException("Access denied - you can only cancel your own orders");
        }

        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderId}")
    public Order updateOrder(@PathVariable("orderId") String orderId,
                             @Valid @RequestBody UpdateOrderRequest request) {
        String authenticatedUserId = getCurrentUserId();

        Order order = orderService.getOrder(orderId);
        if (!authenticatedUserId.equals(order.getUserId())) {
            throw new RuntimeException("Access denied - you can only update your own orders");
        }

        return orderService.updateOrder(orderId, request);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // 这里返回的是 userId
        }
        throw new RuntimeException("User not authenticated");
    }
}