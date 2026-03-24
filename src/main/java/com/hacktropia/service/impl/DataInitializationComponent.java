package com.hacktropia.service.impl;

import com.hacktropia.domain.UserRole;
import com.hacktropia.modal.Users;
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
                    Users users = Users.builder()
                            .password(passwordEncoder.encode(adminPassword))
                            .email(adminEmail)
                            .fullName("Hacktropia")
                            .role(UserRole.ROLE_ADMIN)
                            .build();

                    Users admin=userRepository.save(users);
                }
    }
}
