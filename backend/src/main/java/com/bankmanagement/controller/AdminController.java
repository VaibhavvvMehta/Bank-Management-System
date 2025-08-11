package com.bankmanagement.controller;

import com.bankmanagement.model.User;
import com.bankmanagement.model.Account;
import com.bankmanagement.model.Transaction;
import com.bankmanagement.model.Role;
import com.bankmanagement.dto.AccountAdminDTO;
import com.bankmanagement.service.UserService;
import com.bankmanagement.service.AccountService;
import com.bankmanagement.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:3004"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    // Dashboard statistics - Bank specific
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            Map<String, Object> stats = new HashMap<>();
            
            List<User> bankUsers = userService.getUsersByBankId(bankId);
            List<Account> bankAccounts = accountService.getAccountsByBankId(bankId);
            List<Transaction> bankTransactions = transactionService.getTransactionsByBankId(bankId);
            
            stats.put("bankName", adminUser.getBank().getBankName());
            stats.put("bankCode", adminUser.getBank().getBankCode());
            stats.put("totalUsers", bankUsers.size());
            stats.put("activeUsers", userService.getActiveUsersByBankId(bankId).size());
            stats.put("totalAccounts", bankAccounts.size());
            stats.put("totalTransactions", bankTransactions.size());
            
            // Calculate totals by account status for this bank
            long activeAccounts = bankAccounts.stream()
                    .filter(account -> account.getAccountStatus().name().equals("ACTIVE"))
                    .count();
            long suspendedAccounts = bankAccounts.stream()
                    .filter(account -> account.getAccountStatus().name().equals("SUSPENDED"))
                    .count();
            long closedAccounts = bankAccounts.stream()
                    .filter(account -> account.getAccountStatus().name().equals("CLOSED"))
                    .count();
            
            stats.put("activeAccounts", activeAccounts);
            stats.put("suspendedAccounts", suspendedAccounts);
            stats.put("closedAccounts", closedAccounts);
            
            // Calculate transaction stats for this bank
            long completedTransactions = bankTransactions.stream()
                    .filter(transaction -> transaction.getTransactionStatus().name().equals("COMPLETED"))
                    .count();
            long pendingTransactions = bankTransactions.stream()
                    .filter(transaction -> transaction.getTransactionStatus().name().equals("PENDING"))
                    .count();
            long cancelledTransactions = bankTransactions.stream()
                    .filter(transaction -> transaction.getTransactionStatus().name().equals("CANCELLED"))
                    .count();
            
            stats.put("completedTransactions", completedTransactions);
            stats.put("pendingTransactions", pendingTransactions);
            stats.put("cancelledTransactions", cancelledTransactions);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch dashboard statistics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // User management - Bank specific
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            List<User> bankUsers = userService.getUsersByBankId(bankId);
            return ResponseEntity.ok(bankUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<User>> getActiveUsers(Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            List<User> users = userService.getActiveUsersByBankId(bankId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/users/inactive")
    public ResponseEntity<List<User>> getInactiveUsers(Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            List<User> allUsers = userService.getUsersByBankId(bankId);
            List<User> inactiveUsers = allUsers.stream()
                    .filter(user -> !user.isActive())
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(inactiveUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name, Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            List<User> allUsers = userService.getUsersByBankId(bankId);
            List<User> matchingUsers = allUsers.stream()
                    .filter(user -> user.getFirstName().toLowerCase().contains(name.toLowerCase()) ||
                                   user.getLastName().toLowerCase().contains(name.toLowerCase()) ||
                                   user.getUsername().toLowerCase().contains(name.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(matchingUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails, Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            User existingUser = userService.getUserById(id);
            if (existingUser == null || !existingUser.getBank().getId().equals(bankId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found or not in your bank"));
            }
            
            // Ensure user remains in the same bank
            userDetails.setBank(existingUser.getBank());
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(Map.of(
                "message", "User updated successfully",
                "user", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id, Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            User existingUser = userService.getUserById(id);
            if (existingUser == null || !existingUser.getBank().getId().equals(bankId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found or not in your bank"));
            }
            
            User user = userService.activateUser(id);
            return ResponseEntity.ok(Map.of(
                "message", "User activated successfully",
                "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id, Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            User existingUser = userService.getUserById(id);
            if (existingUser == null || !existingUser.getBank().getId().equals(bankId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found or not in your bank"));
            }
            
            User user = userService.deactivateUser(id);
            return ResponseEntity.ok(Map.of(
                "message", "User deactivated successfully",
                "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request, Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            User existingUser = userService.getUserById(id);
            if (existingUser == null || !existingUser.getBank().getId().equals(bankId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found or not in your bank"));
            }
            
            String roleStr = request.get("role");
            Role role = Role.valueOf(roleStr.toUpperCase());
            
            existingUser.setRole(role);
            User updatedUser = userService.updateUser(id, existingUser);
            
            return ResponseEntity.ok(Map.of(
                "message", "User role updated successfully",
                "user", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            User existingUser = userService.getUserById(id);
            if (existingUser == null || !existingUser.getBank().getId().equals(bankId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found or not in your bank"));
            }
            
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    // Account management - Bank specific
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountAdminDTO>> getAllAccounts(Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            List<Account> bankAccounts = accountService.getAccountsByBankId(bankId);
            List<AccountAdminDTO> accountDTOs = bankAccounts.stream()
                    .map(AccountAdminDTO::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(accountDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Transaction management - Bank specific
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions(Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            Long bankId = adminUser.getBank().getId();
            
            List<Transaction> bankTransactions = transactionService.getTransactionsByBankId(bankId);
            return ResponseEntity.ok(bankTransactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Create new customer for this bank
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user, Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            
            // Set the bank for the new user
            user.setBank(adminUser.getBank());
            
            // Ensure new user is a customer, not admin
            user.setRole(Role.CUSTOMER);
            
            User newUser = userService.createUser(user);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get bank information
    @GetMapping("/bank-info")
    public ResponseEntity<Map<String, Object>> getBankInfo(Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            
            Map<String, Object> bankInfo = new HashMap<>();
            bankInfo.put("bankId", adminUser.getBank().getId());
            bankInfo.put("bankName", adminUser.getBank().getBankName());
            bankInfo.put("bankCode", adminUser.getBank().getBankCode());
            bankInfo.put("address", adminUser.getBank().getAddress());
            bankInfo.put("contactNumber", adminUser.getBank().getContactNumber());
            bankInfo.put("email", adminUser.getBank().getEmail());
            
            return ResponseEntity.ok(bankInfo);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch bank information: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // System operations
    @PostMapping("/create-employee")
    public ResponseEntity<?> createEmployee(@RequestBody User employeeData, Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            
            // Set the bank for the new employee
            employeeData.setBank(adminUser.getBank());
            employeeData.setRole(Role.EMPLOYEE);
            User createdEmployee = userService.createUser(employeeData);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Employee created successfully",
                "user", createdEmployee
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody User adminData, Authentication authentication) {
        try {
            User adminUser = userService.getUserByUsername(authentication.getName());
            
            // Set the bank for the new admin
            adminData.setBank(adminUser.getBank());
            adminData.setRole(Role.ADMIN);
            User createdAdmin = userService.createUser(adminData);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Admin created successfully",
                "user", createdAdmin
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", e.getMessage()));
        }
    }
}
