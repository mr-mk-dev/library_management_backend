package com.hacktropia.service.impl;

import com.hacktropia.domain.BookLoanStatus;
import com.hacktropia.mapper.BookReviewMapper;
import com.hacktropia.modal.Book;
import com.hacktropia.modal.BookLoan;
import com.hacktropia.modal.BookReview;
import com.hacktropia.modal.Users;
import com.hacktropia.payload.dto.BookReviewDTO;
import com.hacktropia.payload.request.CreateReviewRequest;
import com.hacktropia.payload.request.updateReviewRequest;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.repository.BookLoanRepository;
import com.hacktropia.repository.BookRepository;
import com.hacktropia.repository.BookReviewRepository;
import com.hacktropia.service.BookReviewService;
import com.hacktropia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookReviewServiceImpl implements BookReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final BookReviewMapper bookReviewMapper;
    private final BookLoanRepository bookLoanRepository;

    @Override
    public BookReviewDTO createReview(CreateReviewRequest request) throws Exception {
        Users users =userService.getCurrentUser();
        Book book=bookRepository.findById(request.getBookId())
                .orElseThrow(()-> new Exception("book not found!"));
        if(bookReviewRepository.existsByUsersIdAndBookId(users.getId(),book.getId())){
            throw new Exception("you have already reviewed this book!");
        }
        boolean hasReadBook=hasUserReadBook(users.getId(),book.getId());
        if(!hasReadBook){
            throw new Exception("You have not read this book!");
        }
        BookReview bookReview=new BookReview();
        bookReview.setUsers(users);
        bookReview.setBook(book);
        bookReview.setRating(request.getRating());
        bookReview.setReviewText(request.getReviewText());
        bookReview.setTitle(request.getTitle());
        BookReview savedBookReview=bookReviewRepository.save(bookReview);
        return bookReviewMapper.toDTO(savedBookReview);
    }

    @Override
    public BookReviewDTO updateReview(Long reviewId, updateReviewRequest request) throws Exception {
       Users users =userService.getCurrentUser();
       BookReview bookReview=bookReviewRepository.findById(reviewId)
               .orElseThrow(()-> new Exception("review not found!"));
       if(!bookReview.getUsers().getId().equals(users.getId())){
           throw new Exception("You have not reviewed this book!");
       }
       bookReview.setReviewText(request.getReviewText());
       bookReview.setTitle(request.getTitle());
       bookReview.setRating(request.getRating());
       BookReview savedBookReview=bookReviewRepository.save(bookReview);
        return bookReviewMapper.toDTO(savedBookReview);
    }

    @Override
    public void deleteReview(Long reviewId) throws Exception {

        Users currentUsers =userService.getCurrentUser();
        BookReview bookReview=bookReviewRepository.findById(reviewId)
                .orElseThrow(()-> new Exception("Review not found with id: "+ reviewId));
        if(!bookReview.getUsers().getId().equals(currentUsers.getId())){
            throw new Exception("You can only delete your own reviews");
        }
        bookReviewRepository.delete(bookReview);
    }

    @Override
    public PageResponse<BookReviewDTO> getReviewsByBookId(Long id, int page, int size) throws Exception {
        Book book=bookRepository.findById(id).orElseThrow(
                ()-> new Exception("Book not found by id!")
        );
        Pageable pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());
        Page<BookReview> reviewPage=bookReviewRepository.findByBook(book,pageable);
        return convertToPageResponse(reviewPage);
    }

    private PageResponse<BookReviewDTO> convertToPageResponse(Page<BookReview> reviewPage) {

        List<BookReviewDTO> reviewDTOs=reviewPage.getContent()
                .stream()
                .map(bookReviewMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                reviewDTOs,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast(),
                reviewPage.isFirst(),
                reviewPage.isEmpty()
        );
    }

    private boolean hasUserReadBook(Long userId, Long bookId){
        List<BookLoan> bookLoans=bookLoanRepository.findByBookId(bookId);
        return bookLoans.stream()
                .anyMatch(loan->loan.getUsers().getId().equals(userId) &&
                        loan.getStatus()== BookLoanStatus.RETURNED);
    }
}
