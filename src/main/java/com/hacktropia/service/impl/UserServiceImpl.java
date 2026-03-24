package com.hacktropia.service.impl;

import com.hacktropia.mapper.UserMapper;
import com.hacktropia.modal.Users;
import com.hacktropia.payload.dto.UserDTO;
import com.hacktropia.repository.UserRepository;
import com.hacktropia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Users getCurrentUser() throws Exception {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        Users users =userRepository.findByEmail(email);
        if(users ==null){
            throw new Exception("user not found!");
        }
        return users;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<Users> users=userRepository.findAll();

        return users.stream().map(
                UserMapper::toDTO
        ).collect(Collectors.toList());
    }

    @Override
    public Users findById(Long id) throws Exception {
        return userRepository.findById(id).orElseThrow(
                ()-> new Exception("User not found with given id!")
        );
    }
}
