package com.hacktropia.service.impl;

import com.hacktropia.configration.JwtProvider;
import com.hacktropia.domain.UserRole;
import com.hacktropia.exception.UserException;
import com.hacktropia.mapper.UserMapper;
import com.hacktropia.modal.PasswordResetToken;
import com.hacktropia.modal.Users;
import com.hacktropia.payload.dto.UserDTO;
import com.hacktropia.payload.response.AuthResponse;
import com.hacktropia.repository.PasswordResetTokenRepository;
import com.hacktropia.repository.UserRepository;
import com.hacktropia.service.AuthService;
import com.hacktropia.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserServiceImplementation customUserServiceImplementation;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Override
    public AuthResponse login(String username, String password) throws UserException {
        Authentication authentication=authenticate(username,password);

        SecurityContextHolder.getContext().setAuthentication(authentication);
//        Collection<? extends GrantedAuthority> authorities=authentication.getAuthorities();
//        String role= authorities.iterator().next().getAuthority();
        String token= jwtProvider.generateToken(authentication);

        Users users =userRepository.findByEmail(username);

        users.setLastLogin(LocalDateTime.now());
        userRepository.save(users);

        AuthResponse response=new AuthResponse();
        response.setTitle("Login success");
        response.setMessage("Welcome Back" + username);
        response.setJwt(token);
        response.setUser(UserMapper.toDTO(users));
        return response;
    }

    private Authentication authenticate(String username, String password) throws UserException {
        UserDetails userDetails=customUserServiceImplementation.loadUserByUsername(username);

        if(userDetails == null){
            throw new UserException("user not found with email - "+password);
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new UserException("password not match");
        }
        return new UsernamePasswordAuthenticationToken(username,null,userDetails.getAuthorities());
    }

    @Override
    public AuthResponse signup(UserDTO req) throws UserException {
        Users users =userRepository.findByEmail(req.getEmail());

        if(users !=null){
            throw new UserException("email id already registered");
        }
        Users createdUsers =new Users();
        createdUsers.setEmail(req.getEmail());
        createdUsers.setPassword(passwordEncoder.encode(req.getPassword()));
        createdUsers.setPhone(req.getPhone());
        createdUsers.setFullName(req.getFullName());
        createdUsers.setLastLogin(LocalDateTime.now());
        createdUsers.setRole(UserRole.USER);

        Users savedUsers =userRepository.save(createdUsers);
        Authentication auth= new UsernamePasswordAuthenticationToken(
                savedUsers.getEmail(), savedUsers.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt=jwtProvider.generateToken(auth);
        AuthResponse response=new AuthResponse();
        response.setJwt(jwt);
        response.setTitle("Welcome "+ createdUsers.getFullName());
        response.setMessage("register success");
        response.setUser(UserMapper.toDTO(savedUsers));
        return response;

    }

    @Transactional
    public void createPasswordResetToken(String email) throws UserException {

        String frontendUrl="http://localhost:5173";
        Users users =userRepository.findByEmail(email);
        if(users ==null){
            throw new UserException("user not found with given email");
        }
        String token= UUID.randomUUID().toString();

        PasswordResetToken resetToken=PasswordResetToken.builder()
                .token(token)
                .users(users)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();
        passwordResetTokenRepository.save(resetToken);
        String resetLink=frontendUrl+token;
        String subject="Password Reset Request";
        String body="You requested to reset your password. Use this Link (valid 5 minutes):" + resetLink;

        emailService.sendEmail(users.getEmail(),subject,body);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) throws Exception {

        PasswordResetToken resetToken=passwordResetTokenRepository.findByToken(token)
                .orElseThrow(
                        ()-> new Exception("token not valid")
                );

        if(resetToken.isExpired()){
            passwordResetTokenRepository.delete(resetToken);
            throw new Exception("token expired");
        }

        Users users =resetToken.getUsers();
        users.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(users);
        passwordResetTokenRepository.delete(resetToken);
    }
}