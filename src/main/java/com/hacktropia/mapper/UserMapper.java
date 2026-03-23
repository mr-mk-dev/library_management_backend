package com.hacktropia.mapper;

import com.hacktropia.modal.User;
import com.hacktropia.payload.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public static UserDTO toDTO(User user){
        UserDTO userDTO=new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFullName(user.getFullName());
        userDTO.setPhone(user.getPhone());
        userDTO.setLastLogin(user.getLastLogin());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

    public static List<UserDTO> toDTOList(List<User> users){
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
    public static Set<UserDTO> toDTOSet(Set<User> users){
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public static User toEntity(UserDTO userDTO){
        User createdUser=new User();
//        createdUser.setId(user.getId());
        createdUser.setEmail(userDTO.getEmail());
        createdUser.setPassword(userDTO.getPassword());
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setFullName(userDTO.getFullName());
        createdUser.setPhone(userDTO.getPhone());
        createdUser.setRole(userDTO.getRole());

        return createdUser;
    }

}
