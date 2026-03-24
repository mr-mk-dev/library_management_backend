package com.hacktropia.controller;

import com.hacktropia.domain.FineStatus;
import com.hacktropia.domain.FineType;
import com.hacktropia.payload.dto.FineDTO;
import com.hacktropia.payload.request.CreateFineRequest;
import com.hacktropia.payload.request.WaiveFineRequest;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.payload.response.PaymentInitiateResponse;
import com.hacktropia.service.FineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fines")
@Tag(name = "Fines", description = "Fine creation, payment (via RazorPay), and waiver management")
public class FineController {

    private final FineService fineService;

    @PostMapping
    @Operation(summary = "Create a fine", description = "Creates a new fine for a user linked to a book loan. Types: OVERDUE, DAMAGE, LOSS, PROCESSING")
    public ResponseEntity<?> createFine(
            @Valid @RequestBody CreateFineRequest fineRequest
    ) throws Exception {
        FineDTO fineDTO = fineService.createFine(fineRequest);
        return ResponseEntity.ok(fineDTO);
    }


    @PostMapping("/{id}/pay")
    @Operation(summary = "Pay a fine", description = "Initiates a RazorPay payment for the fine. Returns a payment link URL.")
    public ResponseEntity<?> payFine(
            @Parameter(description = "Fine ID") @PathVariable Long id,
            @Parameter(description = "Optional transaction ID") @RequestParam(required = false) String transactionId
    ) throws Exception {
        PaymentInitiateResponse res = fineService.payFine(id, transactionId);
        return ResponseEntity.ok(res);
    }


    @PostMapping("/waive")
    @Operation(summary = "Waive a fine (Admin)", description = "Admin: waives a fine with a reason. Sets status to WAIVED.")
    public ResponseEntity<?> waiveFine(
            @Valid @RequestBody WaiveFineRequest waiveFineRequest
    ) throws Exception {
        FineDTO fineDTO = fineService.waiveFine(waiveFineRequest);
        return ResponseEntity.ok(fineDTO);
    }

    @GetMapping("/my")
    @Operation(summary = "Get my fines", description = "Returns the current user's fines, optionally filtered by status and type")
    public ResponseEntity<?> getMyFines(
            @Parameter(description = "Filter by fine status") @RequestParam(required = false) FineStatus status,
            @Parameter(description = "Filter by fine type") @RequestParam(required = false) FineType type
    ) throws Exception {
        List<FineDTO> fines = fineService.getMyFines(status, type);
        return ResponseEntity.ok(fines);
    }

    @GetMapping
    @Operation(summary = "Get all fines (Admin)", description = "Admin: paginated list of all fines with optional filters")
    public ResponseEntity<?> getAllFines(
            @Parameter(description = "Filter by status") @RequestParam(required = false) FineStatus status,
            @Parameter(description = "Filter by type") @RequestParam(required = false) FineType type,
            @Parameter(description = "Filter by user ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<FineDTO> fines = fineService
                .getAllFines(status, type, userId, page, size);
        return ResponseEntity.ok(fines);
    }

}
