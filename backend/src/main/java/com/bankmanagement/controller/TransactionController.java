package com.bankmanagement.controller;

import com.bankmanagement.model.Transaction;
import com.bankmanagement.model.TransactionStatus;
import com.bankmanagement.model.TransactionType;
import com.bankmanagement.model.User;
import com.bankmanagement.service.TransactionService;
import com.bankmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    // Customer transaction endpoints
    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> createDeposit(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            Long accountId = Long.valueOf(request.get("accountId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = request.getOrDefault("description", "Deposit").toString();
            
            // Verify account ownership for customers
            User user = userService.getUserByUsername(authentication.getName());
            if (user.getRole().name().equals("CUSTOMER")) {
                // Add verification logic here if needed
            }
            
            Transaction transaction = transactionService.createDeposit(accountId, amount, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Deposit successful");
            response.put("transaction", transaction);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/withdrawal")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> createWithdrawal(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            Long accountId = Long.valueOf(request.get("accountId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = request.getOrDefault("description", "Withdrawal").toString();
            
            // Verify account ownership for customers
            User user = userService.getUserByUsername(authentication.getName());
            if (user.getRole().name().equals("CUSTOMER")) {
                // Add verification logic here if needed
            }
            
            Transaction transaction = transactionService.createWithdrawal(accountId, amount, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Withdrawal successful");
            response.put("transaction", transaction);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> createTransfer(@RequestBody Map<String, Object> request, Authentication authentication) {
        try {
            Long fromAccountId = Long.valueOf(request.get("fromAccountId").toString());
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = request.getOrDefault("description", "Transfer").toString();
            
            // Handle both toAccountId and toAccountNumber
            Long toAccountId;
            if (request.containsKey("toAccountId")) {
                toAccountId = Long.valueOf(request.get("toAccountId").toString());
            } else if (request.containsKey("toAccountNumber")) {
                String toAccountNumber = request.get("toAccountNumber").toString();
                toAccountId = transactionService.getAccountIdByAccountNumber(toAccountNumber);
            } else {
                throw new RuntimeException("Either toAccountId or toAccountNumber must be provided");
            }
            
            // Verify account ownership for customers
            User user = userService.getUserByUsername(authentication.getName());
            if (user.getRole().name().equals("CUSTOMER")) {
                // Add verification logic here if needed
            }
            
            Transaction transaction = transactionService.createTransfer(fromAccountId, toAccountId, amount, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Transfer successful");
            response.put("transaction", transaction);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    // Get transactions
    @GetMapping("/my-transactions")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getMyTransactions(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        List<Transaction> transactions = transactionService.getTransactionsByUserId(user.getId());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable Long accountId, Authentication authentication) {
        // Add verification for customer role
        List<Transaction> transactions = transactionService.getTransactionsByAccountIdOrderByDate(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/date-range")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(accountId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id, Authentication authentication) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            // Add verification for customer role
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/reference/{reference}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> getTransactionByReference(@PathVariable String reference, Authentication authentication) {
        try {
            Transaction transaction = transactionService.getTransactionByReference(reference);
            // Add verification for customer role
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        }
    }

    // Admin/Employee endpoints
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getTransactionsByType(@PathVariable String type) {
        try {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            List<Transaction> transactions = transactionService.getTransactionsByType(transactionType);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable String status) {
        try {
            TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
            List<Transaction> transactions = transactionService.getTransactionsByStatus(transactionStatus);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> updateTransactionStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            TransactionStatus transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
            Transaction updatedTransaction = transactionService.updateTransactionStatus(id, transactionStatus);
            
            return ResponseEntity.ok(Map.of(
                "message", "Transaction status updated successfully",
                "transaction", updatedTransaction
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> cancelTransaction(@PathVariable Long id) {
        try {
            Transaction cancelledTransaction = transactionService.cancelTransaction(id);
            return ResponseEntity.ok(Map.of(
                "message", "Transaction cancelled successfully",
                "transaction", cancelledTransaction
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/limits")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getTransactionLimits() {
        Map<String, Object> limits = new HashMap<>();
        limits.put("dailyWithdrawalLimit", transactionService.getDailyWithdrawalLimit());
        limits.put("dailyTransferLimit", transactionService.getDailyTransferLimit());
        limits.put("maxSingleTransaction", transactionService.getMaxSingleTransactionLimit());
        limits.put("minTransactionAmount", transactionService.getMinTransactionAmount());
        
        return ResponseEntity.ok(limits);
    }
}
