package com.hacktropia.service;

import com.hacktropia.modal.User;
import com.hacktropia.payload.dto.UserDTO;

import java.util.List;

public interface UserService {

    public User getCurrentUser() throws Exception;
    public List<UserDTO> getAllUsers();
    User findById(Long id) throws Exception;
}
