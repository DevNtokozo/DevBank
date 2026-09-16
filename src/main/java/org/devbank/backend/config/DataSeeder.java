package org.devbank.backend.config;

import org.devbank.backend.entity.Account;
import org.devbank.backend.entity.User;
import org.devbank.backend.repository.AccountRepository;
import org.devbank.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            UserRepository userRepository,
            AccountRepository accountRepository) {

        return args -> {

            // ==========================================
            // CUSTOMER 1
            // ==========================================

            User testUser = userRepository
                    .findByUsername("testuser")
                    .orElseGet(() -> {

                        User user = new User();

                        user.setUsername("testuser");
                        user.setPassword("Test@123");
                        user.setRole("CUSTOMER");
                        user.setStatus("ACTIVE");

                        return userRepository.save(user);
                    });

            // Account 1000000001
            if (accountRepository
                    .findByAccountNumber("1000000001")
                    .isEmpty()) {

                Account account = new Account();

                account.setUser(testUser);
                account.setAccountNumber("1000000001");
                account.setAccountType("CHEQUE");
                account.setBalance(new BigDecimal("25000.00"));
                account.setStatus("ACTIVE");

                accountRepository.save(account);
            }

            // Account 1000000002
            if (accountRepository
                    .findByAccountNumber("1000000002")
                    .isEmpty()) {

                Account secondAccount = new Account();

                secondAccount.setUser(testUser);
                secondAccount.setAccountNumber("1000000002");
                secondAccount.setAccountType("SAVINGS");
                secondAccount.setBalance(new BigDecimal("10000.00"));
                secondAccount.setStatus("ACTIVE");

                accountRepository.save(secondAccount);
            }


            // ==========================================
            // CUSTOMER 2
            // ==========================================

            User recipient = userRepository
                    .findByUsername("recipient")
                    .orElseGet(() -> {

                        User user = new User();

                        user.setUsername("recipient");
                        user.setPassword("Recipient@123");
                        user.setRole("CUSTOMER");
                        user.setStatus("ACTIVE");

                        return userRepository.save(user);
                    });

            // Account 1000000003
            if (accountRepository
                    .findByAccountNumber("1000000003")
                    .isEmpty()) {

                Account recipientAccount = new Account();

                recipientAccount.setUser(recipient);
                recipientAccount.setAccountNumber("1000000003");
                recipientAccount.setAccountType("CHEQUE");
                recipientAccount.setBalance(new BigDecimal("15000.00"));
                recipientAccount.setStatus("ACTIVE");

                accountRepository.save(recipientAccount);
            }

            System.out.println("==========================================");
            System.out.println("Dev Bank test data loaded successfully");
            System.out.println("==========================================");
            System.out.println("Customer 1: testuser");
            System.out.println("Account: 1000000001");
            System.out.println("Balance: R25,000.00");
            System.out.println("------------------------------------------");
            System.out.println("Account: 1000000002");
            System.out.println("Balance: R10,000.00");
            System.out.println("------------------------------------------");
            System.out.println("Customer 2: recipient");
            System.out.println("Account: 1000000003");
            System.out.println("Balance: R15,000.00");
            System.out.println("==========================================");
        };
    }
}