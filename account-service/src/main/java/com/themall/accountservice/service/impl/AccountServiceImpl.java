package com.themall.accountservice.service.impl;

import com.themall.accountservice.dto.AccountRequest;
import com.themall.accountservice.dto.LoginRequest;
import com.themall.accountservice.entity.Account;
import com.themall.accountservice.repository.AccountRepository;
import com.themall.accountservice.service.AccountService;
import com.themall.accountservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, JwtUtil jwtUtil) {
        this.accountRepository = accountRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Account createAccount(AccountRequest request) {
        if (accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(request.getPassword());

        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setUsername(request.getUsername());
        account.setPassword(encodedPassword);
        account.setShippingAddress(request.getShippingAddress());
        account.setBillingAddress(request.getBillingAddress());
        account.setPaymentMethod(request.getPaymentMethod());

        return accountRepository.save(account);
    }

    // 更新账户信息
    @Override
    public Account updateAccount(String accountId, AccountRequest request) {
        // 0. 查找账户，不存在则抛出异常:RuntimeException 当账户不存在时
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // 1. 仅更新非空字段 - update username, pw, shipping address, billing address, payment method
        if (request.getUsername() != null) {
            account.setUsername(request.getUsername());
        }
        if (request.getPassword() != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            account.setPassword(encoder.encode(request.getPassword()));
        }
        if (request.getShippingAddress() != null) {
            account.setShippingAddress(request.getShippingAddress());
        }
        if (request.getBillingAddress() != null) {
            account.setBillingAddress(request.getBillingAddress());
        }
        if (request.getPaymentMethod() != null) {
            account.setPaymentMethod(request.getPaymentMethod());
        }
        // 保存更新
        return accountRepository.save(account);
    }

    // 获取账户信息
    @Override
    public Account getAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    // 用户登录
    @Override
    public String login(LoginRequest request) {
        // 根据邮箱查找账户
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 验证密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 生成并返回JWT令牌
        return jwtUtil.generateToken(account.getAccountId());
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}