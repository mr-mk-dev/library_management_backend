package com.hacktropia.service;

import com.hacktropia.exception.UserException;
import com.hacktropia.payload.dto.UserDTO;
import com.hacktropia.payload.response.AuthResponse;

public interface AuthService {

    AuthResponse login(String username, String password) throws UserException;
    AuthResponse signup(UserDTO req) throws UserException;

    void createPasswordResetToken(String email) throws UserException;
    void resetPassword(String token, String newPassword) throws Exception;
}
