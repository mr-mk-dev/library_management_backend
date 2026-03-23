package com.hacktropia.service;

import com.hacktropia.payload.dto.WishlistDTO;
import com.hacktropia.payload.response.PageResponse;

public interface WishlistService {
    WishlistDTO addToWishlist(Long bookId, String notes) throws Exception;

    void removeFromWishlist(Long bookId) throws Exception;

    PageResponse<WishlistDTO> getMyWishlist(int page, int size) throws Exception;


}
