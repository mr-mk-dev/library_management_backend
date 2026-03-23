package com.hacktropia.service.impl;

import com.hacktropia.domain.UserRole;
import com.hacktropia.modal.User;
import com.hacktropia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializationComponent implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){
        initializeAdminUser();
    }

    private void initializeAdminUser(){
        String adminEmail="luvag0707@gmail.com";
        String adminPassword="luv";

                if(userRepository.findByEmail(adminEmail) == null){
                    User user= User.builder()
                            .password(passwordEncoder.encode(adminPassword))
                            .email(adminEmail)
                            .fullName("Hacktropia")
                            .role(UserRole.ROLE_ADMIN)
                            .build();

                    User admin=userRepository.save(user);
                }
    }
}
