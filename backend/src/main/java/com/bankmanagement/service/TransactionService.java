package com.bankmanagement.service;

import com.bankmanagement.model.*;
import com.bankmanagement.repository.TransactionRepository;
import com.bankmanagement.exception.InsufficientBalanceException;
import com.bankmanagement.exception.InvalidAmountException;
import com.bankmanagement.exception.AccountNotActiveException;
import com.bankmanagement.exception.DailyLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountService accountService;
    
    // Daily limits for different transaction types
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("50000.00");
    private static final BigDecimal DAILY_TRANSFER_LIMIT = new BigDecimal("100000.00");
    private static final BigDecimal MAX_SINGLE_TRANSACTION = new BigDecimal("100000.00");
    private static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("1.00");
    
    @Transactional
    public Transaction createDeposit(Long accountId, BigDecimal amount, String description) {
        // Validate amount
        validateTransactionAmount(amount);
        
        Account account = accountService.getAccountById(accountId);
        
        // Validate account status
        validateAccountStatus(account);
        
        // Additional validation for deposits
        if (amount.compareTo(MAX_SINGLE_TRANSACTION) > 0) {
            throw new InvalidAmountException("Single deposit amount cannot exceed ₹" + MAX_SINGLE_TRANSACTION);
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setDescription(description != null ? description : "Deposit to account");
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
        // Validate amount
        validateTransactionAmount(amount);
        
        Account account = accountService.getAccountById(accountId);
        
        // Validate account status
        validateAccountStatus(account);
        
        // Check current balance
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                String.format("Insufficient balance. Available: ₹%.2f, Requested: ₹%.2f", 
                    account.getBalance(), amount)
            );
        }
        
        // Check daily withdrawal limit
        BigDecimal todayWithdrawals = getTodayWithdrawalAmount(accountId);
        if (todayWithdrawals.add(amount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            throw new DailyLimitExceededException(
                String.format("Daily withdrawal limit exceeded. Limit: ₹%.2f, Today's withdrawals: ₹%.2f, Requested: ₹%.2f", 
                    DAILY_WITHDRAWAL_LIMIT, todayWithdrawals, amount)
            );
        }
        
        // Check single transaction limit
        if (amount.compareTo(MAX_SINGLE_TRANSACTION) > 0) {
            throw new InvalidAmountException("Single withdrawal amount cannot exceed ₹" + MAX_SINGLE_TRANSACTION);
        }
        
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setDescription(description != null ? description : "Withdrawal from account");
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
        // Validate amount
        validateTransactionAmount(amount);
        
        // Validate different accounts
        if (fromAccountId.equals(toAccountId)) {
            throw new InvalidAmountException("Cannot transfer to the same account");
        }
        
        Account fromAccount = accountService.getAccountById(fromAccountId);
        Account toAccount = accountService.getAccountById(toAccountId);
        
        // Validate account statuses
        validateAccountStatus(fromAccount);
        validateAccountStatus(toAccount);
        
        // Check current balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                String.format("Insufficient balance in source account. Available: ₹%.2f, Requested: ₹%.2f", 
                    fromAccount.getBalance(), amount)
            );
        }
        
        // Check daily transfer limit
        BigDecimal todayTransfers = getTodayTransferAmount(fromAccountId);
        if (todayTransfers.add(amount).compareTo(DAILY_TRANSFER_LIMIT) > 0) {
            throw new DailyLimitExceededException(
                String.format("Daily transfer limit exceeded. Limit: ₹%.2f, Today's transfers: ₹%.2f, Requested: ₹%.2f", 
                    DAILY_TRANSFER_LIMIT, todayTransfers, amount)
            );
        }
        
        // Check single transaction limit
        if (amount.compareTo(MAX_SINGLE_TRANSACTION) > 0) {
            throw new InvalidAmountException("Single transfer amount cannot exceed ₹" + MAX_SINGLE_TRANSACTION);
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
    
    // Get transactions by bank ID
    public List<Transaction> getTransactionsByBankId(Long bankId) {
        return transactionRepository.findByBankId(bankId);
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
    
    public Long getAccountIdByAccountNumber(String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        return account.getId();
    }
    
    private String generateTransactionReference() {
        String reference;
        do {
            reference = "TXN" + System.currentTimeMillis() + new Random().nextInt(10000);
        } while (transactionRepository.findByTransactionReference(reference).isPresent());
        
        return reference;
    }
    
    // Validation helper methods
    private void validateTransactionAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidAmountException("Transaction amount cannot be null");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Transaction amount must be greater than zero");
        }
        
        if (amount.compareTo(MIN_TRANSACTION_AMOUNT) < 0) {
            throw new InvalidAmountException("Minimum transaction amount is ₹" + MIN_TRANSACTION_AMOUNT);
        }
        
        // Check for too many decimal places (max 2)
        if (amount.scale() > 2) {
            throw new InvalidAmountException("Amount cannot have more than 2 decimal places");
        }
    }
    
    private void validateAccountStatus(Account account) {
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(
                String.format("Account %s is not active. Current status: %s", 
                    account.getAccountNumber(), account.getAccountStatus())
            );
        }
    }
    
    // Daily limit checking methods
    private BigDecimal getTodayWithdrawalAmount(Long accountId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        List<Transaction> todayWithdrawals = transactionRepository
            .findByFromAccountIdAndTransactionTypeAndCreatedAtBetweenAndTransactionStatus(
                accountId, TransactionType.WITHDRAWAL, startOfDay, endOfDay, TransactionStatus.COMPLETED
            );
        
        return todayWithdrawals.stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal getTodayTransferAmount(Long accountId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        
        List<Transaction> todayTransfers = transactionRepository
            .findByFromAccountIdAndTransactionTypeAndCreatedAtBetweenAndTransactionStatus(
                accountId, TransactionType.TRANSFER, startOfDay, endOfDay, TransactionStatus.COMPLETED
            );
        
        return todayTransfers.stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Getters for limits (useful for frontend)
    public BigDecimal getDailyWithdrawalLimit() {
        return DAILY_WITHDRAWAL_LIMIT;
    }
    
    public BigDecimal getDailyTransferLimit() {
        return DAILY_TRANSFER_LIMIT;
    }
    
    public BigDecimal getMaxSingleTransactionLimit() {
        return MAX_SINGLE_TRANSACTION;
    }
    
    public BigDecimal getMinTransactionAmount() {
        return MIN_TRANSACTION_AMOUNT;
    }
}
