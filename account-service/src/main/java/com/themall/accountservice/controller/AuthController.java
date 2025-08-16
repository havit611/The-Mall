package com.themall.accountservice.controller;

import com.themall.accountservice.dto.LoginRequest;
import com.themall.accountservice.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// 处理登录和Token验证相关请求
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AccountService accountService;
    
    @Autowired
    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    // 用户登录
    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {
        // return JWT Token String
        return accountService.login(request);
    }

    // 检查用户的登录状态是否仍然有效
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authHeader) {
        // 检查请求头是否存在且格式正确
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(false);
        }

        // 提取token
        String token = authHeader.substring(7);
        // calling accountService to validate token
        boolean isValid = accountService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}