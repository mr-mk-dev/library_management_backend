package com.hacktropia.service;

import com.hacktropia.payload.dto.BookReviewDTO;
import com.hacktropia.payload.request.CreateReviewRequest;
import com.hacktropia.payload.request.updateReviewRequest;
import com.hacktropia.payload.response.PageResponse;

public interface BookReviewService {

    BookReviewDTO createReview(CreateReviewRequest request) throws Exception;

    BookReviewDTO updateReview(Long reviewId, updateReviewRequest request) throws Exception;

    void deleteReview(Long reviewId) throws Exception;
    PageResponse<BookReviewDTO> getReviewsByBookId(Long id, int page, int size) throws Exception;
}
