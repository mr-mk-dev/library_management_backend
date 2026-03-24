package com.hacktropia.mapper;

import com.hacktropia.modal.BookReview;
import com.hacktropia.payload.dto.BookReviewDTO;
import org.springframework.stereotype.Component;

@Component
public class BookReviewMapper {

    public BookReviewDTO toDTO(BookReview bookReview){
        if(bookReview==null){
            return null;
        }
        return BookReviewDTO.builder()
                .id(bookReview.getId())
                .userId(bookReview.getUsers().getId())
                .userName(bookReview.getUsers().getFullName())
                .bookId(bookReview.getBook().getId())
                .bookTitle(bookReview.getBook().getTitle())
                .rating(bookReview.getRating())
                .reviewText(bookReview.getReviewText())
                .title(bookReview.getTitle())
                .createdAt(bookReview.getCreatedAt())
                .updatedAt(bookReview.getUpdatedAt())
                .build();
    }
}
