package com.hacktropia.repository;

import com.hacktropia.modal.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Page<Wishlist> findByUsersId(Long userId, Pageable pageable);
    Wishlist findByUsersIdAndBookId(Long userId, Long booId);
    boolean existsByUsersIdAndBookId(Long userId, Long bookId);
}
