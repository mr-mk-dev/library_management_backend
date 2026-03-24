package com.hacktropia.repository;

import com.hacktropia.modal.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,Long> {

    Users findByEmail(String email);
}
