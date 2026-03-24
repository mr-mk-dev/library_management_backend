package com.hacktropia.controller;

import com.hacktropia.domain.BookLoanStatus;
import com.hacktropia.payload.dto.BookLoanDTO;
import com.hacktropia.payload.request.BookLoanSearchRequest;
import com.hacktropia.payload.request.CheckinRequest;
import com.hacktropia.payload.request.CheckoutRequest;
import com.hacktropia.payload.request.RenewalRequest;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.service.BookLoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-loans")
@Tag(name = "Book Loans", description = "Book checkout, checkin, renewal, and overdue management")
public class BookLoanController {

    private final BookLoanService bookLoanService;

    @PostMapping("/checkout")
    @Operation(summary = "Checkout a book", description = "Checks out a book for the current user. Requires active subscription. Due date = checkout date + subscription's maxDaysPerBook.")
    public ResponseEntity<?> checkoutBook(
            @Valid @RequestBody CheckoutRequest checkoutRequest) throws Exception {

            BookLoanDTO bookLoan=bookLoanService.checkoutBook(checkoutRequest);
            return new ResponseEntity<>(bookLoan, HttpStatus.CREATED);
    }

    @PostMapping("/checkout/user/{userId}")
    @Operation(summary = "Checkout book for specific user (Admin)", description = "Admin endpoint: checks out a book on behalf of a specific user")
    public ResponseEntity<?> checkoutBookForUser(
            @Parameter(description = "Target user ID") @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest checkoutRequest
    ) throws Exception{
        BookLoanDTO bookLoan=bookLoanService
                .checkoutBookForUser(userId,checkoutRequest);
        return new ResponseEntity<>(bookLoan,HttpStatus.CREATED);

    }


    @PostMapping("/checkin")
    @Operation(summary = "Return a book", description = "Processes a book return. Increments available copies and sets return date.")
    public ResponseEntity<?> checkin(
            @Valid @RequestBody CheckinRequest checkinRequest
    ) throws Exception{
        BookLoanDTO bookLoan=bookLoanService
                .checkinBook(checkinRequest);
        return new ResponseEntity<>(bookLoan,HttpStatus.OK);

    }


    @PostMapping("/renew")
    @Operation(summary = "Renew a book loan", description = "Extends the due date. Max 2 renewals allowed, book must not be overdue.")
    public ResponseEntity<?> renew(
            @Valid @RequestBody RenewalRequest renewalRequest
            ) throws Exception{
        BookLoanDTO bookLoan=bookLoanService
                .renewCheckout(renewalRequest);
        return new ResponseEntity<>(bookLoan,HttpStatus.OK);

    }


    @GetMapping("/my")
    @Operation(summary = "Get my book loans", description = "Returns the current user's book loans, optionally filtered by status")
    public ResponseEntity<?> getMyBookLoans(
            @Parameter(description = "Filter by loan status") @RequestParam(required = false) BookLoanStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size)
    throws Exception{
            PageResponse<BookLoanDTO> bookLoans=bookLoanService
                    .getMyBookLoans(status,page,size);
        return ResponseEntity.ok(bookLoans);

    }

    @PostMapping("/search")
    @Operation(summary = "Search all book loans", description = "Admin: search all book loans with filters (userId, bookId, status, date range)")
    public ResponseEntity<?> getAllBookLoans(
            @RequestBody BookLoanSearchRequest searchRequest
            ) throws Exception {
        PageResponse<BookLoanDTO> bookLoans= bookLoanService
                .getBookLoans(searchRequest);
        return ResponseEntity.ok(bookLoans);
    }

    @PostMapping("/admin/update-overdue")
    @Operation(summary = "Update overdue book loans (Admin)", description = "Batch job: finds all CHECKED_OUT loans past due date and marks them as OVERDUE")
    public ResponseEntity<?> updateOverdueBookLoans(){

        int updateCount= bookLoanService.updateOverdueBookLoan();
        return ResponseEntity.ok(
                new ApiResponse(
                        "overdue book loans are updated", true
                )
        );
    }
}
