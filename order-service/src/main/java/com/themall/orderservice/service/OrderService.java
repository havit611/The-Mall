package com.themall.orderservice.service;

import com.themall.orderservice.dto.request.CreateOrderRequest;
import com.themall.orderservice.dto.request.UpdateOrderRequest;
import com.themall.orderservice.entity.Order;

public interface OrderService {
    
    Order createOrder(CreateOrderRequest request);
    
    void cancelOrder(String orderId);
    
    Order updateOrder(String orderId, UpdateOrderRequest request);
    
    Order getOrder(String orderId);
}