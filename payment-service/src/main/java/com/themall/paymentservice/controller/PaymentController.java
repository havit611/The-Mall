package com.themall.paymentservice.controller;

import com.themall.paymentservice.dto.PaymentRequest;
import com.themall.paymentservice.entity.Payment;
import com.themall.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment management", description = "payment-related APIs, including submitting payments, checking payment status, and issuing refunds.")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // submitPayment - 提交新的支付请求，通过 Idempotency-Key 请求头实现幂等性，防止重复支付
    @Operation(summary = "Submit Payment", description = "Submit a new payment request with idempotency support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment created successfully",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "409", description = "Idempotency key conflict")
    })
    @PostMapping
    public ResponseEntity<Payment> submitPayment(
            @Parameter(description = "Payment request information", required = true)
            @Valid @RequestBody PaymentRequest request,
            @Parameter(description = "Idempotency key，ensure the same request is not processed multiple times", required = true)
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        
        Payment payment = paymentService.submitPayment(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    // updatePayment -- 根据支付ID更新支付信息
    @Operation(summary = "Update payment information", description = "Update information for the specified payment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update successful",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PutMapping("/{paymentId}")
    public ResponseEntity<Payment> updatePayment(
            @Parameter(description = "PaymentID", required = true)
            @PathVariable String paymentId) {
        Payment payment = paymentService.updatePayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    // reversePayment - 对指定支付ID执行退款操作，将支付状态改为 REFUNDED
    @Operation(summary = "Refund Request", description = "Refund for specific paymentID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refund success",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
            @ApiResponse(responseCode = "400", description = "Payment status does not allow refunds")
    })
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Payment> reversePayment(
            @Parameter(description = "PaymentID", required = true)
            @PathVariable String paymentId) {
        Payment payment = paymentService.reversePayment(paymentId);
        return ResponseEntity.ok(payment); // 返回200
    }

    // getPayment - 根据支付ID查询支付详细信息，用于获取支付状态和详情
    @Operation(summary = "Query payment information", description = "Retrieve payment details by payment ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Query successful",
                    content = @Content(schema = @Schema(implementation = Payment.class))),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(
            @Parameter(description = "PaymentID", required = true)
            @PathVariable String paymentId) {
        Payment payment = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(payment);
    }
}