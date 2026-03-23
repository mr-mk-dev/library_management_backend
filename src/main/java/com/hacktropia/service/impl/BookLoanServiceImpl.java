package com.hacktropia.service.impl;

import com.hacktropia.domain.BookLoanStatus;
import com.hacktropia.domain.BookLoanType;
import com.hacktropia.exception.BookException;
import com.hacktropia.mapper.BookLoanMapper;
import com.hacktropia.modal.Book;
import com.hacktropia.modal.BookLoan;
import com.hacktropia.modal.Subscription;
import com.hacktropia.modal.User;
import com.hacktropia.payload.dto.BookLoanDTO;
import com.hacktropia.payload.dto.SubscriptionDTO;
import com.hacktropia.payload.request.BookLoanSearchRequest;
import com.hacktropia.payload.request.CheckinRequest;
import com.hacktropia.payload.request.CheckoutRequest;
import com.hacktropia.payload.request.RenewalRequest;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.repository.BookLoanRepository;
import com.hacktropia.repository.BookRepository;
import com.hacktropia.service.BookLoanService;
import com.hacktropia.service.SubscriptionService;
import com.hacktropia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import javax.naming.Referenceable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookLoanServiceImpl implements BookLoanService {

    private final BookLoanRepository bookLoanRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final BookRepository bookRepository;
    private final BookLoanMapper bookLoanMapper;
    private final Referenceable referenceable;

    @Override
    public BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest) throws Exception {

        User user=userService.getCurrentUser();


        return checkoutBookForUser(user.getId(),checkoutRequest);
    }

    @Override
    public BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws Exception {

        User user=userService.findById(userId);

        SubscriptionDTO subscription=subscriptionService.getUsersActiveSubscription(user.getId());

        Book book=bookRepository.findById(checkoutRequest.getBookId())
                .orElseThrow(()-> new Exception("Book not found with id"+ checkoutRequest.getBookId()));

        if(!book.getActive()){
            throw new BookException("Book is not active");
        }
        if(book.getAvailableCopies()<=0){
            throw new BookException("Book is not available");
        }

        if(bookLoanRepository.hasActiveCheckout(userId, book.getId())){
            throw new BookException("Book already has active checkout");
        }

        long activeCheckouts=bookLoanRepository.countActiveBookLoansByUser(userId);
        int maxBooksAllowed=subscription.getMaxBooksAllowed();
        if(activeCheckouts>=maxBooksAllowed){
            throw new Exception("you have reached your maximum number of books allowed");
        }

        long overdueCount=bookLoanRepository.countOverdueBookLoansByUser(userId);
        if(overdueCount>0){
            throw new Exception("first return old overdue book!");
        }

        BookLoan bookLoan=BookLoan
                .builder()
                .user(user)
                .book(book)
                .type(BookLoanType.CHECKOUT)
                .status(BookLoanStatus.CHECKED_OUT)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(checkoutRequest.getCheckoutDays()))
                .renewalCount(0)
                .maxRenewals(2)
                .notes(checkoutRequest.getNotes())
                .isOverdue(false)
                .overdueDays(0)
                .build();

        book.setAvailableCopies(book.getAvailableCopies()-1);
        bookRepository.save(book);
        BookLoan savedBookLoan=bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public BookLoanDTO checkinBook(CheckinRequest checkinRequest) throws Exception {

        BookLoan bookLoan=bookLoanRepository.findById(checkinRequest.getBookLoanId())
                .orElseThrow(()-> new Exception("bookloan not found!"));

        if(!bookLoan.isActive()){
            throw new Exception("book is not active");
        }
        bookLoan.setReturnDate(LocalDate.now());

        BookLoanStatus condition=checkinRequest.getCondition();
        if(condition==null){
            condition=BookLoanStatus.RETURNED;
        }
        bookLoan.setStatus(condition);
        bookLoan.setOverdueDays(0);
        bookLoan.setIsOverdue(false);
        bookLoan.setNotes("book returned by user");

        if(condition!=BookLoanStatus.LOST){
            Book book=bookLoan.getBook();
            book.setAvailableCopies(book.getAvailableCopies()+1);
            bookRepository.save(book);

            }

        BookLoan savedBookLoan=bookLoanRepository.save(bookLoan);
        return bookLoanMapper.toDTO(savedBookLoan);

    }

    @Override
    public BookLoanDTO renewCheckout(RenewalRequest renewalRequest) throws Exception {

        BookLoan bookLoan=bookLoanRepository.findById(renewalRequest.getBookLoanId())
                .orElseThrow(()-> new Exception("bookloan not found!"));

        if(!bookLoan.canRenew()){
            throw new BookException("book cannot be renewed");
        }
        bookLoan.setDueDate(bookLoan.getDueDate().plusDays(renewalRequest.getExtensionDays()));
        bookLoan.setRenewalCount(bookLoan.getRenewalCount()+1);
        bookLoan.setNotes("book renewed by user");
        BookLoan savedBookLoan=bookLoanRepository.save(bookLoan);

        return bookLoanMapper.toDTO(savedBookLoan);
    }

    @Override
    public PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) throws Exception {
        User currentUser=userService.getCurrentUser();
        Page<BookLoan> bookLoanPage;
        if(status!=null){
            Pageable  pageable= PageRequest.of(page,size, Sort.by("dueDate").ascending());
            bookLoanPage=bookLoanRepository.findByStatusAndUser(
                    status,currentUser,pageable
            );
        }else {
            Pageable  pageable= PageRequest.of(page,size, Sort.by("createdAt").descending());
            bookLoanPage=bookLoanRepository.findByUserId(currentUser.getId(),pageable);

        }
        return convertToPageResponse(bookLoanPage);
    }

    @Override
    public PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest searchRequest) {

        Pageable pageable=createPageable(
                searchRequest.getPage(),
                searchRequest.getSize(),
                searchRequest.getSortBy(),
                searchRequest.getSortDirection()
        );

        Page<BookLoan> bookLoanPage;

        if(Boolean.TRUE.equals(searchRequest.getOverdueOnly())){
            bookLoanPage=bookLoanRepository.findOverdueBookLoans(LocalDate.now(),pageable);
        } else if (searchRequest.getUserId()!=null) {
            bookLoanPage=bookLoanRepository.findByUserId(searchRequest.getUserId(),pageable);
        } else if (searchRequest.getBookId()!=null) {
            bookLoanPage=bookLoanRepository.findByBookId(searchRequest.getBookId(), pageable);
        } else if (searchRequest.getStatus()!=null) {
            bookLoanPage=bookLoanRepository.findByStatus(searchRequest.getStatus(),pageable);
        } else if (searchRequest.getStartDate()!=null && searchRequest.getEndDate()!=null) {
            bookLoanPage=bookLoanRepository.findBookLoansByDateRange(
                    searchRequest.getStartDate(),
                    searchRequest.getEndDate(),
                    pageable);
        }else{
            bookLoanPage=bookLoanRepository.findAll(pageable);
        }
        return convertToPageResponse(bookLoanPage);

    }

    @Override
    public int updateOverdueBookLoan() {


        Pageable pageable=PageRequest.of(0,1000);
        Page<BookLoan> overduePage=bookLoanRepository
                .findOverdueBookLoans(LocalDate.now(),pageable);

        int updateCount=0;
        for(BookLoan bookLoan : overduePage.getContent()){
            if(bookLoan.getStatus() == BookLoanStatus.CHECKED_OUT){
                bookLoan.setStatus(BookLoanStatus.OVERDUE);
                bookLoan.setIsOverdue(true);

//                int overdueDays = fineCalculationService.calculateOverdueDays(
//                        bookLoan.getDueDate(), LocalDate.now()
//                );
//                bookLoan.setOverdueDays(overdueDays);
                int overdueDays=calculateOverdueDate(bookLoan.getDueDate(),LocalDate.now());

//                BigDecimal fine= fineCalculationService.calculateOverdueFine(bookLoan);

                bookLoanRepository.save(bookLoan);
                updateCount++;
            }
        }
        return updateCount;
    }

    private Pageable createPageable(int page,
                                    int size,
                                    String sortBy,
                                    String sortDirection){
        size=Math.min(size,100);
        size=Math.max(size,1);

        Sort sort=sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        return PageRequest.of(page,size,sort);
    }
private PageResponse<BookLoanDTO> convertToPageResponse(Page<BookLoan> bookLoanPage){
    List<BookLoanDTO> bookLoanDTOS=bookLoanPage.getContent()
            .stream()
            .map(bookLoanMapper::toDTO)
            .collect(Collectors.toList());

    return new PageResponse<>(
            bookLoanDTOS,
            bookLoanPage.getNumber(),
            bookLoanPage.getSize(),
            bookLoanPage.getTotalElements(),
            bookLoanPage.getTotalPages(),
            bookLoanPage.isLast(),
            bookLoanPage.isFirst(),
            bookLoanPage.isEmpty()
    );
}

    public int calculateOverdueDate(LocalDate dueDate, LocalDate today){
        if(today.isBefore(dueDate) || today.isEqual(dueDate)){
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dueDate,today);
    }


}
