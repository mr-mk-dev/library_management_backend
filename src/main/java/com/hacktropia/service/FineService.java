package com.hacktropia.service;

import com.hacktropia.domain.FineStatus;
import com.hacktropia.domain.FineType;
import com.hacktropia.modal.Fine;
import com.hacktropia.payload.dto.FineDTO;
import com.hacktropia.payload.request.CreateFineRequest;
import com.hacktropia.payload.request.WaiveFineRequest;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.payload.response.PaymentInitiateResponse;

import java.util.List;

public interface FineService {

    FineDTO createFine(CreateFineRequest createFineRequest) throws Exception;

    PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception;

    void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception;

    FineDTO waiveFine(WaiveFineRequest waiveFineRequest) throws Exception;

    List<FineDTO> getMyFines(FineStatus status, FineType type) throws Exception;

    PageResponse<FineDTO> getAllFines(
            FineStatus status,
            FineType type,
            Long userId,
            int page,
            int size
    );

}
