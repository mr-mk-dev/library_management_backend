package com.hacktropia.mapper;

import com.hacktropia.modal.Users;
import com.hacktropia.payload.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public static UserDTO toDTO(Users users){
        UserDTO userDTO=new UserDTO();
        userDTO.setId(users.getId());
        userDTO.setEmail(users.getEmail());
        userDTO.setFullName(users.getFullName());
        userDTO.setPhone(users.getPhone());
        userDTO.setLastLogin(users.getLastLogin());
        userDTO.setRole(users.getRole());

        return userDTO;
    }

    public static List<UserDTO> toDTOList(List<Users> users){
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }
    public static Set<UserDTO> toDTOSet(Set<Users> users){
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toSet());
    }

    public static Users toEntity(UserDTO userDTO){
        Users createdUsers =new Users();
//        createdUser.setId(user.getId());
        createdUsers.setEmail(userDTO.getEmail());
        createdUsers.setPassword(userDTO.getPassword());
        createdUsers.setCreatedAt(LocalDateTime.now());
        createdUsers.setFullName(userDTO.getFullName());
        createdUsers.setPhone(userDTO.getPhone());
        createdUsers.setRole(userDTO.getRole());

        return createdUsers;
    }

}
