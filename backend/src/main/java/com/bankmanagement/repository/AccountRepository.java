package com.bankmanagement.repository;

import com.bankmanagement.model.Account;
import com.bankmanagement.model.AccountStatus;
import com.bankmanagement.model.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUserId(Long userId);
    List<Account> findByAccountType(AccountType accountType);
    List<Account> findByAccountStatus(AccountStatus accountStatus);
    boolean existsByAccountNumber(String accountNumber);
    
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountStatus = :status")
    List<Account> findByUserIdAndAccountStatus(@Param("userId") Long userId, 
                                              @Param("status") AccountStatus status);
    
    @Query("SELECT a FROM Account a WHERE a.balance > :amount")
    List<Account> findAccountsWithBalanceGreaterThan(@Param("amount") BigDecimal amount);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // Bank-specific queries
    List<Account> findByBankId(Long bankId);
    List<Account> findByBankIdAndAccountStatus(Long bankId, AccountStatus status);
    
    @Query("SELECT a FROM Account a WHERE a.bank.id = :bankId")
    List<Account> findAllByBankId(@Param("bankId") Long bankId);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.bank.id = :bankId")
    long countByBankId(@Param("bankId") Long bankId);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.bank.id = :bankId AND a.accountStatus = :status")
    long countByBankIdAndAccountStatus(@Param("bankId") Long bankId, @Param("status") AccountStatus status);
}
