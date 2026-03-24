package com.hacktropia.repository;

import com.hacktropia.domain.BookLoanStatus;
import com.hacktropia.modal.BookLoan;
import com.hacktropia.modal.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {
    Page<BookLoan> findByUsersId(Long userId, Pageable pageable);

    Page<BookLoan> findByStatusAndUsers(BookLoanStatus status, Users users, Pageable pageable);

    Page<BookLoan> findByStatus(BookLoanStatus status, Pageable pageable);

    Page<BookLoan> findByBookId(Long bookId, Pageable pageable);

    List<BookLoan> findByBookId(Long bookId);

    @Query(" select case when count(bl) > 0 then true else false end from BookLoan bl" +
            " where bl.users.id =:userId and bl.book.id=:bookId " +
            " and (bl.status='CHECKED_OUT' OR bl.status='OVERDUE')")
    boolean hasActiveCheckout(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId
    );

    @Query("SELECT COUNT(bl) FROM BookLoan  bl WHERE bl.users.id = :userId " +
            " AND (bl.status = 'CHECKED_OUT' OR bl.status='OVERDUE')")
    long countActiveBookLoansByUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(bl) FROM BookLoan bl WHERE bl.users.id=:userId " +
            " AND bl.status='OVERDUE' ")
    long countOverdueBookLoansByUser(@Param("userId") Long userId);

    @Query("SELECT  bl FROM BookLoan bl WHERE bl.dueDate< :currentDate " +
            " AND (bl.status='CHECKED_OUT' OR bl.status='OVERDUE')")
    Page<BookLoan> findOverdueBookLoans(@Param("currentDate") LocalDate currentDate,
                                        Pageable pageable);

    @Query("SELECT bl FROM BookLoan bl WHERE bl.checkoutDate BETWEEN :startDate AND :endDate")
    Page<BookLoan> findBookLoansByDateRange(
            @Param("startDate") LocalDate starDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    boolean existsByUsersIdAndBookIdAndStatus(Long userId, Long bookId, BookLoanStatus status);
}
