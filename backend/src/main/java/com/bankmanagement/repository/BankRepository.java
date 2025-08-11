package com.bankmanagement.repository;

import com.bankmanagement.model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findByBankCode(String bankCode);
    Optional<Bank> findByBankName(String bankName);
    List<Bank> findByIsActiveTrue();
    boolean existsByBankCode(String bankCode);
    boolean existsByBankName(String bankName);
}
