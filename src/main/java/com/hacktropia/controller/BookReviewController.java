package com.hacktropia.controller;

import com.hacktropia.payload.dto.BookReviewDTO;
import com.hacktropia.payload.request.CreateReviewRequest;
import com.hacktropia.payload.request.updateReviewRequest;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.service.BookReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Book Reviews", description = "Create, update, delete book reviews. User must have read (returned) the book to review it.")
public class BookReviewController {

    private final BookReviewService bookReviewService;

    @PostMapping
    @Operation(summary = "Create a review", description = "Creates a review for a book. User must have previously returned the book. Rating: 1–5, one review per user per book.")
    public ResponseEntity<?> createReview(
            @Valid @RequestBody CreateReviewRequest request
    ) throws Exception {
        BookReviewDTO reviewDTO=bookReviewService.createReview(request);
        return ResponseEntity.ok(reviewDTO);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update own review", description = "Updates the review text, title, and rating. Only the review author can update.")
    public ResponseEntity<?> updateReview(
            @Parameter(description = "Review ID") @PathVariable Long id,
            @Valid @RequestBody updateReviewRequest request
    ) throws Exception {
        BookReviewDTO reviewDTO=bookReviewService.updateReview(id, request);
        return ResponseEntity.ok(reviewDTO);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Delete own review", description = "Deletes a review. Only the review author can delete.")
    public ResponseEntity<?> deleteReview(
            @Parameter(description = "Review ID") @PathVariable Long reviewId) throws Exception {
        bookReviewService.deleteReview(reviewId);
        return ResponseEntity.ok(
                new ApiResponse("Review deleted successfully", true)
        );
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get reviews for a book", description = "Returns paginated reviews for a specific book, sorted by newest first")
    public ResponseEntity<PageResponse<BookReviewDTO>> getReviewsByBook(
            @Parameter(description = "Book ID") @PathVariable Long bookId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        PageResponse<BookReviewDTO> reviews=bookReviewService
                .getReviewsByBookId(
                        bookId,page,size
                );
        return ResponseEntity.ok(reviews);
    }
}
