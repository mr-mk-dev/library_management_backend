package com.hacktropia.service;

import com.hacktropia.payload.dto.PaymentDTO;
import com.hacktropia.payload.request.PaymentInitiateRequest;
import com.hacktropia.payload.request.PaymentVerifyRequest;
import com.hacktropia.payload.response.PaymentInitiateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    PaymentInitiateResponse initiatePayment(PaymentInitiateRequest req) throws Exception;

    PaymentDTO verifyPayment(PaymentVerifyRequest req) throws Exception;

    Page<PaymentDTO> getAllPayments(Pageable pageable);
}
