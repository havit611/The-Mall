package com.themall.accountservice.controller;

import com.themall.accountservice.dto.AccountRequest;
import com.themall.accountservice.entity.Account;
import com.themall.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // 创建新账户
    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountRequest request) {
        Account account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    // 更新账户信息
    @PutMapping("/{accountId}")
    public Account updateAccount(@PathVariable("accountId") String accountId,
                                @Valid @RequestBody AccountRequest request) {
        return accountService.updateAccount(accountId, request);
    }

    // 获取账户信息
    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable("accountId") String accountId) {
        return accountService.getAccount(accountId);
    }
}