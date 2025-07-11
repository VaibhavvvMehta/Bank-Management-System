package com.bankmanagement.service;

import com.bankmanagement.model.Account;
import com.bankmanagement.model.AccountStatus;
import com.bankmanagement.model.User;
import com.bankmanagement.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserService userService;
    
    public Account createAccount(Account account, Long userId) {
        User user = userService.getUserById(userId);
        account.setUser(user);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setAccountStatus(AccountStatus.ACTIVE);
        
        return accountRepository.save(account);
    }
    
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = getAccountById(id);
        
        account.setAccountType(accountDetails.getAccountType());
        account.setAccountStatus(accountDetails.getAccountStatus());
        
        return accountRepository.save(account);
    }
    
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }
    
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found with number: " + accountNumber));
    }
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }
    
    public List<Account> getActiveAccountsByUserId(Long userId) {
        return accountRepository.findByUserIdAndAccountStatus(userId, AccountStatus.ACTIVE);
    }
    
    public List<Account> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findByAccountStatus(status);
    }
    
    public void deleteAccount(Long id) {
        Account account = getAccountById(id);
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete account with positive balance");
        }
        accountRepository.delete(account);
    }
    
    public Account suspendAccount(Long id) {
        Account account = getAccountById(id);
        account.setAccountStatus(AccountStatus.SUSPENDED);
        return accountRepository.save(account);
    }
    
    public Account activateAccount(Long id) {
        Account account = getAccountById(id);
        account.setAccountStatus(AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }
    
    public Account closeAccount(Long id) {
        Account account = getAccountById(id);
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot close account with positive balance");
        }
        account.setAccountStatus(AccountStatus.CLOSED);
        return accountRepository.save(account);
    }
    
    public Account updateBalance(Long id, BigDecimal newBalance) {
        Account account = getAccountById(id);
        account.setBalance(newBalance);
        return accountRepository.save(account);
    }
    
    public Account addBalance(Long id, BigDecimal amount) {
        Account account = getAccountById(id);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }
    
    public Account deductBalance(Long id, BigDecimal amount) {
        Account account = getAccountById(id);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        account.setBalance(account.getBalance().subtract(amount));
        return accountRepository.save(account);
    }
    
    public BigDecimal getAccountBalance(Long id) {
        Account account = getAccountById(id);
        return account.getBalance();
    }
    
    public long getAccountCountByUserId(Long userId) {
        return accountRepository.countByUserId(userId);
    }
    
    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "ACC" + System.currentTimeMillis() + new Random().nextInt(1000);
        } while (accountRepository.existsByAccountNumber(accountNumber));
        
        return accountNumber;
    }
}
