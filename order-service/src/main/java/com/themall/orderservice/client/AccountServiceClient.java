package com.themall.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * TODO: 微服务集成待完成项：
 * - 远程调用AccountServiceClient验证用户合法性
 * - 远程调用ItemServiceClient进行库存检查和扣减
 * - 添加Feign客户端的circuit breaker and fallback handling
 * - 添加Kafka消费者处理跨服务事件(支付完成: 1. 用户在支付服务完成支付; 2. 支付服务发送消息到 payment-events 主题; 3. 订单服务消费该消息; 4. 更新订单状态从 CREATED → PAID; 5. 发送订单确认通知事件)
 * - Zipkin，Prometheus + Grafana 加服务调用的监控和追踪
 */

// 根据访问地址，调用远程的微服务account-service
@FeignClient(name = "account-service", url = "http://localhost:8081")
public interface AccountServiceClient {

    // 获取account info
    @GetMapping("/api/accounts/{accountId}")
    Account getAccount(@PathVariable String accountId);
    
    // DTO for Account Service response
    record Account(String accountId, String username, String email, 
                   String firstName, String lastName, String phoneNumber, 
                   java.time.LocalDateTime createdAt) {}
}