package com.hacktropia.controller;

import com.hacktropia.payload.dto.WishlistDTO;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/add/{bookId}")
    public ResponseEntity<?> addToWishlist(@PathVariable Long bookId,
                                           @PathVariable(required = false) String notes) throws Exception {
        WishlistDTO wishlistDTO=wishlistService.addToWishlist(bookId,notes);
        return ResponseEntity.ok(wishlistDTO);
    }

    @DeleteMapping("/remove/{bookId}")
    public ResponseEntity<ApiResponse> removeFromWishlist(@PathVariable Long bookId) throws Exception {
        wishlistService.removeFromWishlist(bookId);
        return ResponseEntity.ok(new ApiResponse("Book removed from wishlist successfully", true));
    }
    @GetMapping("/my-wishlist")
    public ResponseEntity<?> getMyWishList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) throws Exception {
        PageResponse<WishlistDTO> wishlist=wishlistService
                .getMyWishlist(page,size);
        return ResponseEntity.ok(wishlist);
    }
}
