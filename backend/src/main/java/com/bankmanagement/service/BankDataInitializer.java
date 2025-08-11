package com.bankmanagement.service;

import com.bankmanagement.model.*;
import com.bankmanagement.repository.BankRepository;
import com.bankmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BankDataInitializer implements CommandLineRunner {
    
    @Autowired
    private BankRepository bankRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeBanksAndAdmins();
    }
    
    private void initializeBanksAndAdmins() {
        System.out.println("🔄 Initializing banks and admin users...");
        
        // Create Bank1 if doesn't exist
        if (!bankRepository.existsByBankCode("BANK1")) {
            System.out.println("📦 Creating Bank1...");
            Bank bank1 = new Bank("BANK1", "Bank1", 
                    "123 Main Street, Downtown", "+1-555-0101", "admin@bank1.com");
            bank1 = bankRepository.save(bank1);
            
            // Create admin for Bank1
            createBankAdmin(bank1, "bank1admin", "admin@bank1.com", "Bank1", "Admin");
        } else {
            System.out.println("✅ Bank1 already exists - skipping creation");
        }
        
        // Create Bank2 if doesn't exist
        if (!bankRepository.existsByBankCode("BANK2")) {
            System.out.println("📦 Creating Bank2...");
            Bank bank2 = new Bank("BANK2", "Bank2", 
                    "456 Business Avenue, Financial District", "+1-555-0202", "admin@bank2.com");
            bank2 = bankRepository.save(bank2);
            
            // Create admin for Bank2
            createBankAdmin(bank2, "bank2admin", "admin@bank2.com", "Bank2", "Admin");
        } else {
            System.out.println("✅ Bank2 already exists - skipping creation");
        }
        
        System.out.println("✅ Bank initialization complete!");
    }
    
    private void createBankAdmin(Bank bank, String username, String email, String firstName, String lastName) {
        if (!userRepository.existsByUsername(username)) {
            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail(email);
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setPhoneNumber("+1-555-ADMIN");
            admin.setAddress("Bank Admin Office");
            admin.setRole(Role.ADMIN);
            admin.setBank(bank);
            admin.setActive(true);
            
            userRepository.save(admin);
            
            System.out.println("👤 Created admin for " + bank.getBankName() + ":");
            System.out.println("   Username: " + username);
            System.out.println("   Password: admin123");
            System.out.println("   Email: " + email);
            System.out.println("   Bank: " + bank.getBankName() + " (" + bank.getBankCode() + ")");
            System.out.println();
        } else {
            System.out.println("✅ Admin user '" + username + "' already exists - skipping creation");
        }
    }
}
