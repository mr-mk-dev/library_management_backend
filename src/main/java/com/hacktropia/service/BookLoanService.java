package com.hacktropia.service;

import com.hacktropia.domain.BookLoanStatus;
import com.hacktropia.payload.dto.BookLoanDTO;
import com.hacktropia.payload.request.BookLoanSearchRequest;
import com.hacktropia.payload.request.CheckinRequest;
import com.hacktropia.payload.request.CheckoutRequest;
import com.hacktropia.payload.request.RenewalRequest;
import com.hacktropia.payload.response.PageResponse;

public interface BookLoanService {

    BookLoanDTO checkoutBook(CheckoutRequest checkoutRequest) throws Exception;

    BookLoanDTO checkoutBookForUser(Long userId, CheckoutRequest checkoutRequest) throws Exception;
    BookLoanDTO checkinBook(CheckinRequest checkinRequest) throws Exception;
    BookLoanDTO renewCheckout(RenewalRequest renewalRequest) throws Exception;
    PageResponse<BookLoanDTO> getMyBookLoans(BookLoanStatus status, int page, int size) throws Exception;
    PageResponse<BookLoanDTO> getBookLoans(BookLoanSearchRequest request);
    int updateOverdueBookLoan();



}
