package com.themall.accountservice.service;

import com.themall.accountservice.dto.AccountRequest;
import com.themall.accountservice.dto.LoginRequest;
import com.themall.accountservice.entity.Account;

public interface AccountService {
    Account createAccount(AccountRequest request);
    Account updateAccount(String accountId, AccountRequest request);
    Account getAccount(String accountId);
    String login(LoginRequest request);
    boolean validateToken(String token);
}