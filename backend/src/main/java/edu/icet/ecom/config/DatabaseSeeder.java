package edu.icet.ecom.config;

import edu.icet.ecom.model.entity.User;
import edu.icet.ecom.model.enums.UserStatus;
import edu.icet.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Check if our root admin already exists
        if (userRepository.findByEmail("admin@rentals.com").isEmpty()) {
            log.warn("No Root Admin found in the database. Seeding default Admin...");

            User admin = new User();
            admin.setUserName("System Admin");
            admin.setEmail("admin@rentals.com");
            // We MUST encode the password before saving, otherwise login will fail!
            admin.setPassword(passwordEncoder.encode("securePassword123"));
            admin.setRole(UserStatus.ADMIN); // Ensure this matches your Enum perfectly

            userRepository.save(admin);
            log.info("Default Admin created successfully!");
            log.info("Email: admin@rentals.com");
            log.info("Password: securePassword123");
        } else {
            log.info("Root Admin is already present in the database.");
        }
    }
}