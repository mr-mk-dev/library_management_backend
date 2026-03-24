package com.hacktropia.controller;

import com.hacktropia.exception.UserException;
import com.hacktropia.payload.dto.UserDTO;
import com.hacktropia.payload.request.ForgotPasswordRequest;
import com.hacktropia.payload.request.LoginRequest;
import com.hacktropia.payload.request.ResetPasswordRequest;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.payload.response.AuthResponse;
import com.hacktropia.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, and password management — no JWT required")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Creates a new user account with ROLE_USER and returns a JWT token")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email already registered")
    })
    public ResponseEntity<AuthResponse> signupHandler(
            @RequestBody UserDTO req
            ) throws UserException {
        AuthResponse res=authService.signup(req);
        return ResponseEntity.ok(res);
    }


    @PostMapping("/login")
    @Operation(summary = "Login with email and password", description = "Authenticates the user and returns a JWT token valid for 24 hours")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful — JWT returned"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> loginHandler(
            @Valid  @RequestBody  LoginRequest req
    ) throws UserException {
        AuthResponse res=authService.login(req.getEmail(),req.getPassword());
        return ResponseEntity.ok(res);
    }
    

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset email", description = "Sends a password reset link to the user's email (link valid for 5 minutes)")
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
    @Operation(summary = "Reset password using token", description = "Resets the password using the token received via email")
    public ResponseEntity<ApiResponse> signupHandler(
            @RequestBody ResetPasswordRequest request) throws Exception {
       authService.resetPassword(request.getToken(), request.getPassword());
       ApiResponse res= new ApiResponse(
               "Password reset successful", true
       );
        return ResponseEntity.ok(res);
    }

}
