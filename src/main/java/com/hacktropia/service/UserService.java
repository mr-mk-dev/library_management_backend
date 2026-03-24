package com.hacktropia.service;

import com.hacktropia.modal.Users;
import com.hacktropia.payload.dto.UserDTO;

import java.util.List;

public interface UserService {

    public Users getCurrentUser() throws Exception;
    public List<UserDTO> getAllUsers();
    Users findById(Long id) throws Exception;
}
