package com.bankmanagement.config;

import com.bankmanagement.model.User;
import com.bankmanagement.model.Role;
import com.bankmanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
// @Component - Disabled to use new BankDataInitializer
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create a simple test customer for easy login
        createTestUser("test", "test123", "test@bank.com", "Test", "User", Role.CUSTOMER);
        
        // Check if admin user exists
        try {
            userService.getUserByUsername("admin");
            System.out.println("Admin user already exists - Username: admin, Password: admin123");
        } catch (Exception e) {
            // Create admin user if not exists
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123"); // Don't encode here, let createUser do it
            admin.setEmail("admin@bank.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPhoneNumber("1234567890");
            admin.setAddress("Admin Address");
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            
            userService.createUser(admin);
            System.out.println("Admin user created - Username: admin, Password: admin123");
        }

        // Check if demo customer exists
        try {
            userService.getUserByUsername("demo");
            System.out.println("Demo user already exists - Username: demo, Password: demo123");
        } catch (Exception e) {
            // Create demo customer if not exists
            User demo = new User();
            demo.setUsername("demo");
            demo.setPassword("demo123"); // Don't encode here, let createUser do it
            demo.setEmail("demo@bank.com");
            demo.setFirstName("Demo");
            demo.setLastName("User");
            demo.setPhoneNumber("9876543210");
            demo.setAddress("Demo Address");
            demo.setRole(Role.CUSTOMER);
            demo.setActive(true);
            
            userService.createUser(demo);
            System.out.println("Demo user created - Username: demo, Password: demo123");
        }
        
        System.out.println("\n=== AVAILABLE TEST USERS ===");
        System.out.println("1. Username: test, Password: test123 (Customer)");
        System.out.println("2. Username: admin, Password: admin123 (Admin)");
        System.out.println("3. Username: demo, Password: demo123 (Customer)");
        System.out.println("================================\n");
    }
    
    private void createTestUser(String username, String password, String email, String firstName, String lastName, Role role) {
        try {
            userService.getUserByUsername(username);
            System.out.println(username + " user already exists - Username: " + username + ", Password: " + password);
        } catch (Exception e) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // Don't encode here, let createUser do it
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber("1234567890");
            user.setAddress("Test Address");
            user.setRole(role);
            user.setActive(true);
            
            userService.createUser(user);
            System.out.println(username + " user created - Username: " + username + ", Password: " + password);
        }
    }
}
