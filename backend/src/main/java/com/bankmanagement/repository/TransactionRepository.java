package com.bankmanagement.repository;

import com.bankmanagement.model.Transaction;
import com.bankmanagement.model.TransactionStatus;
import com.bankmanagement.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionReference(String transactionReference);
    List<Transaction> findByFromAccountId(Long fromAccountId);
    List<Transaction> findByToAccountId(Long toAccountId);
    List<Transaction> findByTransactionType(TransactionType transactionType);
    List<Transaction> findByTransactionStatus(TransactionStatus transactionStatus);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);
    
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
           "AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId")
    List<Transaction> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
           "ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(@Param("accountId") Long accountId);
}
