package com.samvidhan.service;

import com.samvidhan.entity.Role;
import com.samvidhan.entity.Status;
import com.samvidhan.entity.User;
import com.samvidhan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        String masterAdminEmail = "nikhilesh6757@gmail.com";
        Optional<User> existingUserOpt = userRepository.findByEmail(masterAdminEmail);
        
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            boolean needsUpdate = false;
            
            if (existingUser.getRole() != Role.ADMIN) {
                existingUser.setRole(Role.ADMIN);
                needsUpdate = true;
            }
            if (existingUser.getStatus() != Status.APPROVED) {
                existingUser.setStatus(Status.APPROVED);
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                userRepository.save(existingUser);
                System.out.println("Master Admin role/status granted to existing account.");
            }
        } else {
            User adminUser = new User();
            adminUser.setEmail(masterAdminEmail);
            adminUser.setName("Master Admin");
            adminUser.setPassword("__oauth_google__"); 
            adminUser.setRole(Role.ADMIN);
            adminUser.setStatus(Status.APPROVED);
            userRepository.save(adminUser);
            System.out.println("Master Admin account pre-seeded successfully.");
        }
    }
}
