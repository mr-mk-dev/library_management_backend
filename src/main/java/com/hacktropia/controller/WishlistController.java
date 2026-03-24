package com.hacktropia.controller;

import com.hacktropia.payload.dto.WishlistDTO;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
@Tag(name = "Wishlist", description = "Personal book wishlist management")
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/add/{bookId}")
    @Operation(summary = "Add book to wishlist", description = "Adds a book to the current user's wishlist. Duplicate entries are not allowed.")
    public ResponseEntity<?> addToWishlist(
            @Parameter(description = "Book ID") @PathVariable Long bookId,
            @Parameter(description = "Optional notes") @PathVariable(required = false) String notes) throws Exception {
        WishlistDTO wishlistDTO = wishlistService.addToWishlist(bookId, notes);
        return ResponseEntity.ok(wishlistDTO);
    }

    @DeleteMapping("/remove/{bookId}")
    @Operation(summary = "Remove book from wishlist", description = "Removes a book from the current user's wishlist")
    public ResponseEntity<ApiResponse> removeFromWishlist(
            @Parameter(description = "Book ID") @PathVariable Long bookId) throws Exception {
        wishlistService.removeFromWishlist(bookId);
        return ResponseEntity.ok(new ApiResponse("Book removed from wishlist successfully", true));
    }

    @GetMapping("/my-wishlist")
    @Operation(summary = "Get my wishlist", description = "Returns the current user's wishlist (paginated, sorted by newest first)")
    public ResponseEntity<?> getMyWishList(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        PageResponse<WishlistDTO> wishlist = wishlistService
                .getMyWishlist(page, size);
        return ResponseEntity.ok(wishlist);
    }
}
