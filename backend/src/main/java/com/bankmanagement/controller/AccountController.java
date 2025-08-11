package com.bankmanagement.controller;

import com.bankmanagement.model.Account;
import com.bankmanagement.model.AccountStatus;
import com.bankmanagement.model.AccountType;
import com.bankmanagement.model.User;
import com.bankmanagement.service.AccountService;
import com.bankmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3004"})
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    // Customer endpoints
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> createAccount(@Valid @RequestBody Account account, Authentication authentication) {
        try {
            User user = userService.getUserByUsername(authentication.getName());
            Account createdAccount = accountService.createAccount(account, user.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Account created successfully");
            response.put("account", createdAccount);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/my-accounts")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Account>> getMyAccounts(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        List<Account> accounts = accountService.getAccountsByUserId(user.getId());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/my-accounts/active")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Account>> getMyActiveAccounts(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        List<Account> accounts = accountService.getActiveAccountsByUserId(user.getId());
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> getAccountById(@PathVariable Long id, Authentication authentication) {
        try {
            Account account = accountService.getAccountById(id);
            User user = userService.getUserByUsername(authentication.getName());
            
            // Check if user owns this account or has admin/employee role
            if (!account.getUser().getId().equals(user.getId()) && 
                !user.getRole().name().equals("ADMIN") && 
                !user.getRole().name().equals("EMPLOYEE")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied"));
            }
            
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> getAccountBalance(@PathVariable Long id, Authentication authentication) {
        try {
            Account account = accountService.getAccountById(id);
            User user = userService.getUserByUsername(authentication.getName());
            
            // Check if user owns this account or has admin/employee role
            if (!account.getUser().getId().equals(user.getId()) && 
                !user.getRole().name().equals("ADMIN") && 
                !user.getRole().name().equals("EMPLOYEE")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Access denied"));
            }
            
            BigDecimal balance = accountService.getAccountBalance(id);
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
        }
    }

    // Admin/Employee endpoints
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Account>> getAccountsByUserId(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<Account>> getAccountsByStatus(@PathVariable String status) {
        try {
            AccountStatus accountStatus = AccountStatus.valueOf(status.toUpperCase());
            List<Account> accounts = accountService.getAccountsByStatus(accountStatus);
            return ResponseEntity.ok(accounts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
        try {
            Account updatedAccount = accountService.updateAccount(id, accountDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Account updated successfully");
            response.put("account", updatedAccount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<?> updateAccountStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            AccountStatus accountStatus = AccountStatus.valueOf(status.toUpperCase());
            Account updatedAccount;
            
            switch (accountStatus) {
                case ACTIVE:
                    updatedAccount = accountService.activateAccount(id);
                    break;
                case SUSPENDED:
                    updatedAccount = accountService.suspendAccount(id);
                    break;
                case CLOSED:
                    updatedAccount = accountService.closeAccount(id);
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid status"));
            }
            
            return ResponseEntity.ok(Map.of(
                "message", "Account status updated successfully",
                "account", updatedAccount
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }
}
