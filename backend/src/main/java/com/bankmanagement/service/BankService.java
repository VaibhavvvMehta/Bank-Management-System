package com.bankmanagement.service;

import com.bankmanagement.model.Bank;
import com.bankmanagement.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {
    
    @Autowired
    private BankRepository bankRepository;
    
    public List<Bank> getAllActiveBanks() {
        return bankRepository.findByIsActiveTrue();
    }
    
    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }
    
    public Bank getBankById(Long id) {
        return bankRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank not found with id: " + id));
    }
    
    public Bank getBankByCode(String bankCode) {
        return bankRepository.findByBankCode(bankCode)
                .orElseThrow(() -> new RuntimeException("Bank not found with code: " + bankCode));
    }
    
    public Bank createBank(Bank bank) {
        if (bankRepository.existsByBankCode(bank.getBankCode())) {
            throw new RuntimeException("Bank with code " + bank.getBankCode() + " already exists");
        }
        if (bankRepository.existsByBankName(bank.getBankName())) {
            throw new RuntimeException("Bank with name " + bank.getBankName() + " already exists");
        }
        return bankRepository.save(bank);
    }
    
    public Bank updateBank(Long id, Bank bankDetails) {
        Bank bank = getBankById(id);
        
        bank.setBankName(bankDetails.getBankName());
        bank.setAddress(bankDetails.getAddress());
        bank.setContactNumber(bankDetails.getContactNumber());
        bank.setEmail(bankDetails.getEmail());
        bank.setIsActive(bankDetails.getIsActive());
        
        return bankRepository.save(bank);
    }
    
    public void deleteBank(Long id) {
        Bank bank = getBankById(id);
        bank.setIsActive(false);
        bankRepository.save(bank);
    }
}
