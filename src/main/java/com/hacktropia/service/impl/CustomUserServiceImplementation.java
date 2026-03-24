package com.hacktropia.service.impl;

import com.hacktropia.modal.Users;
import com.hacktropia.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserServiceImplementation implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserServiceImplementation(UserRepository userRepository){
        this.userRepository= userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users =userRepository.findByEmail(username);

        if(users ==null){
            throw new UsernameNotFoundException("user not exist with username: "+ username);
        }

        GrantedAuthority authority= new SimpleGrantedAuthority(users.getRole().toString());

        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(authority);
        return new org.springframework.security.core.userdetails.User(
                users.getEmail(), users.getPassword(), authorities
        );


    }
}
