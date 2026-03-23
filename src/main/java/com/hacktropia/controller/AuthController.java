package com.hacktropia.controller;

import com.hacktropia.exception.UserException;
import com.hacktropia.payload.dto.UserDTO;
import com.hacktropia.payload.request.ForgotPasswordRequest;
import com.hacktropia.payload.request.LoginRequest;
import com.hacktropia.payload.request.ResetPasswordRequest;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.payload.response.AuthResponse;
import com.hacktropia.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signupHandler(
            @RequestBody UserDTO req
            ) throws UserException {
        AuthResponse res=authService.signup(req);
        return ResponseEntity.ok(res);
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(
            @Valid  @RequestBody  LoginRequest req
    ) throws UserException {
        AuthResponse res=authService.login(req.getEmail(),req.getPassword());
        return ResponseEntity.ok(res);
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) throws UserException {
        authService.createPasswordResetToken(request.getEmail());
        ApiResponse res=new ApiResponse(
                "A reset link was sent to your email.", true
        );

        return ResponseEntity.ok(res);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> signupHandler(
            @RequestBody ResetPasswordRequest request) throws Exception {
       authService.resetPassword(request.getToken(), request.getPassword());
       ApiResponse res= new ApiResponse(
               "Password reset successful", true
       );
        return ResponseEntity.ok(res);
    }

}
