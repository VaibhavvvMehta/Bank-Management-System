package com.bankmanagement.service;

import com.bankmanagement.model.*;
import com.bankmanagement.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountService accountService;
    
    @Transactional
    public Transaction createDeposit(Long accountId, BigDecimal amount, String description) {
        Account account = accountService.getAccountById(accountId);
        
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setToAccount(account);
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        
        // Save transaction first
        transaction = transactionRepository.save(transaction);
        
        // Update account balance
        accountService.addBalance(accountId, amount);
        
        // Update transaction status and balance after transaction
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transaction.setBalanceAfterTransaction(accountService.getAccountBalance(accountId));
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public Transaction createWithdrawal(Long accountId, BigDecimal amount, String description) {
        Account account = accountService.getAccountById(accountId);
        
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setFromAccount(account);
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        
        // Save transaction first
        transaction = transactionRepository.save(transaction);
        
        // Update account balance
        accountService.deductBalance(accountId, amount);
        
        // Update transaction status and balance after transaction
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transaction.setBalanceAfterTransaction(accountService.getAccountBalance(accountId));
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public Transaction createTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {
        Account fromAccount = accountService.getAccountById(fromAccountId);
        Account toAccount = accountService.getAccountById(toAccountId);
        
        if (fromAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Source account is not active");
        }
        
        if (toAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Destination account is not active");
        }
        
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in source account");
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        
        // Save transaction first
        transaction = transactionRepository.save(transaction);
        
        // Update account balances
        accountService.deductBalance(fromAccountId, amount);
        accountService.addBalance(toAccountId, amount);
        
        // Update transaction status and balance after transaction
        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        transaction.setBalanceAfterTransaction(accountService.getAccountBalance(fromAccountId));
        
        return transactionRepository.save(transaction);
    }
    
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }
    
    public Transaction getTransactionByReference(String reference) {
        return transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction not found with reference: " + reference));
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }
    
    public List<Transaction> getTransactionsByAccountIdOrderByDate(Long accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }
    
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByTransactionType(type);
    }
    
    public List<Transaction> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByTransactionStatus(status);
    }
    
    public List<Transaction> getTransactionsByDateRange(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByAccountIdAndDateRange(accountId, startDate, endDate);
    }
    
    public Transaction updateTransactionStatus(Long id, TransactionStatus status) {
        Transaction transaction = getTransactionById(id);
        transaction.setTransactionStatus(status);
        return transactionRepository.save(transaction);
    }
    
    public Transaction cancelTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        
        if (transaction.getTransactionStatus() == TransactionStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed transaction");
        }
        
        transaction.setTransactionStatus(TransactionStatus.CANCELLED);
        return transactionRepository.save(transaction);
    }
    
    private String generateTransactionReference() {
        String reference;
        do {
            reference = "TXN" + System.currentTimeMillis() + new Random().nextInt(10000);
        } while (transactionRepository.findByTransactionReference(reference).isPresent());
        
        return reference;
    }
}
