package com.themall.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


// Validates user identity + retrieves user information: to ensure only legitimate users can create orders.
@FeignClient(name = "account-service", url = "http://localhost:8081")
public interface AccountServiceClient {

    //  get account info
    @GetMapping("/api/accounts/{accountId}")
    Account getAccount(@PathVariable("accountId") String accountId);
    
    // DTO for Account Service response
    record Account(String accountId, String username, String email, 
                   String firstName, String lastName, String phoneNumber, 
                   java.time.LocalDateTime createdAt) {}
}