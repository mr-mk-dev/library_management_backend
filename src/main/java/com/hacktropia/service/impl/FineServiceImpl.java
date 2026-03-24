package com.hacktropia.service.impl;

import com.hacktropia.domain.FineStatus;
import com.hacktropia.domain.FineType;
import com.hacktropia.domain.PaymentGateway;
import com.hacktropia.domain.PaymentType;
import com.hacktropia.mapper.FineMapper;
import com.hacktropia.modal.BookLoan;
import com.hacktropia.modal.Fine;
import com.hacktropia.modal.Users;
import com.hacktropia.payload.dto.FineDTO;
import com.hacktropia.payload.request.CreateFineRequest;
import com.hacktropia.payload.request.PaymentInitiateRequest;
import com.hacktropia.payload.request.WaiveFineRequest;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.payload.response.PaymentInitiateResponse;
import com.hacktropia.repository.BookLoanRepository;
import com.hacktropia.repository.FineRepository;
import com.hacktropia.service.FineService;
import com.hacktropia.service.PaymentService;
import com.hacktropia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final BookLoanRepository bookLoanRepository;
    private final FineRepository fineRepository;
    private final FineMapper fineMapper;
    private final UserService userService;
    private final PaymentService paymentService;
    @Override
    public FineDTO createFine(CreateFineRequest createFineRequest) throws Exception {
        BookLoan bookLoan=bookLoanRepository.findById(createFineRequest.getBookLoanId())
                .orElseThrow(()-> new Exception("Book loan doesn't exist"));

        Fine fine=Fine.builder()
                .bookLoan(bookLoan)
                .users(bookLoan.getUsers())
                .type(createFineRequest.getType())
                .amount(createFineRequest.getAmount())
                .status(FineStatus.PENDING)
                .reason(createFineRequest.getReason())
                .notes(createFineRequest.getNotes())
                .build();
        Fine savedFine=fineRepository.save(fine);
        return fineMapper.toDTO(savedFine);
    }

    @Override
    public PaymentInitiateResponse payFine(Long fineId, String transactionId) throws Exception {
        Fine fine=fineRepository.findById(fineId)
                .orElseThrow(()-> new Exception("Fine doesn't exist"));

        if(fine.getStatus().equals(FineStatus.PAID)){
            throw new Exception("fine already paid");
        }
        if(fine.getStatus().equals(FineStatus.WAIVED)){
            throw new Exception("fine waived");
        }

        Users users = userService.getCurrentUser();
        PaymentInitiateRequest request= PaymentInitiateRequest
                .builder()
                .userId(users.getId())
                .fineId(fine.getId())
                .paymentType(PaymentType.FINE)
                .gateway(PaymentGateway.RAZORPAY)
                .amount(fine.getAmount())
                .description("Library fine payment")
                .build();
        return paymentService.initiatePayment(request);
    }

    @Override
    public void markFineAsPaid(Long fineId, Long amount, String transactionId) throws Exception {

        Fine fine=fineRepository.findById(fineId)
                .orElseThrow(()-> new Exception("Fine not found with id: "+ fineId));

        fine.applyPayment(amount);
        fine.setTransactionId(transactionId);
        fine.setStatus(FineStatus.PAID);
        fine.setUpdatedAt(LocalDateTime.now());

        fineRepository.save(fine);

    }

    @Override
    public FineDTO waiveFine(WaiveFineRequest waiveFineRequest) throws Exception {

        Fine fine=fineRepository.findById(waiveFineRequest.getFineId())
                .orElseThrow(()-> new Exception("Fine not found with id: "));

        if(fine.getStatus()==FineStatus.PAID){
            throw new Exception("Fine has already been paid and cannot be waived");
        }
        Users currentAdmin=userService.getCurrentUser();
        fine.waive(currentAdmin,waiveFineRequest.getReason());

        Fine savedFine=fineRepository.save(fine);
        return fineMapper.toDTO(savedFine);
    }

    @Override
    public List<FineDTO> getMyFines(FineStatus status, FineType type) throws Exception {
        Users currentUsers =userService.getCurrentUser();
        List<Fine> fines;

        if(status != null && type!= null){
            fines=fineRepository.findByUserId(currentUsers.getId()).stream()
                    .filter(f -> f.getStatus()==status && f.getType()==type)
                    .collect(Collectors.toList());
        } else if (status!=null) {
            fines=fineRepository.findByUserId(currentUsers.getId()).stream()
                    .filter(f-> f.getStatus()==status)
                    .collect(Collectors.toList());

        }else if(type!=null){
            fines=fineRepository.findByUserIdAndType(currentUsers.getId(),type);

        }else {
            fines=fineRepository.findByUserId(currentUsers.getId());
        }
        return fines.stream().map(
                fineMapper::toDTO
        ).collect(Collectors.toList());
    }

    @Override
    public PageResponse<FineDTO> getAllFines(FineStatus status, FineType type, Long userId, int page, int size) {

        Pageable pageable= PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );
        Page<Fine>finePage=fineRepository.findAllWithFilters(
                userId,
                status,
                type,
                pageable
        );
        return convertToPageResponse(finePage);

    }

    private PageResponse<FineDTO> convertToPageResponse(Page<Fine> finePage){
        List<FineDTO> fineDTOs=finePage.getContent()
                .stream()
                .map(fineMapper::toDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                fineDTOs,
                finePage.getNumber(),
                finePage.getSize(),
                finePage.getTotalElements(),
                finePage.getTotalPages(),
                finePage.isLast(),
                finePage.isFirst(),
                finePage.isEmpty()
        );
    }
}
