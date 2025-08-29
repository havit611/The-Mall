package com.themall.paymentservice.controller;

import com.themall.paymentservice.dto.PaymentRequest;
import com.themall.paymentservice.entity.Payment;
import com.themall.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // submitPayment - 提交新的支付请求，通过 Idempotency-Key 请求头实现幂等性，防止重复支付
    @PostMapping
    public ResponseEntity<Payment> submitPayment(@RequestBody @Valid PaymentRequest request) {
        Payment payment = paymentService.submitPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    // updatePayment -- 根据支付ID更新支付信息
    @PutMapping("/{paymentId}")
    public ResponseEntity<Payment> updatePayment(@PathVariable("paymentId") String paymentId) {
        Payment payment = paymentService.updatePayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    // reversePayment - 对指定支付ID执行退款操作，将支付状态改为 REFUNDED
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Payment> reversePayment(@PathVariable("paymentId") String paymentId) {
        Payment payment = paymentService.reversePayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    // getPayment - 根据支付ID查询支付详细信息，用于获取支付状态和详情
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(@PathVariable("paymentId") String paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }
}