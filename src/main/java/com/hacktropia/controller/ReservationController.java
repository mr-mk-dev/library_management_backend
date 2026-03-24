package com.hacktropia.controller;

import com.hacktropia.domain.ReservationStatus;
import com.hacktropia.exception.BookException;
import com.hacktropia.payload.dto.ReservationDTO;
import com.hacktropia.payload.request.ReservationRequest;
import com.hacktropia.payload.request.ReservationSearchRequest;
import com.hacktropia.payload.response.PageResponse;
import com.hacktropia.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
@Tag(name = "Reservations", description = "Queue-based book reservations for unavailable books. Max 5 per user.")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping()
    @Operation(summary = "Create a reservation", description = "Creates a reservation for the current user. Book must have 0 available copies. Max 5 active reservations per user.")
    public ResponseEntity<?> createReservation(
            @Valid @RequestBody ReservationRequest reservationRequest
    ) throws Exception {
        ReservationDTO reservationDTO = reservationService
                .createReservation(reservationRequest);
        return ResponseEntity.ok(reservationDTO);
    }

    @PostMapping("/user/{userId}")
    @Operation(summary = "Create reservation for specific user", description = "Creates a reservation on behalf of a specific user")
    public ResponseEntity<?> createReservationForUser(
            @Parameter(description = "Target user ID") @PathVariable Long userId,
            @Valid @RequestBody ReservationRequest reservationRequest
    ) throws Exception {
        ReservationDTO reservation = reservationService
                .createReservationForUser(reservationRequest, userId);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a reservation", description = "Cancels a reservation. Only the owner or ADMIN can cancel. Status must be PENDING or AVAILABLE.")
    public ResponseEntity<?> cancelReservation(
            @Parameter(description = "Reservation ID") @PathVariable Long id
    ) throws Exception {
        ReservationDTO reservation = reservationService.cancelReservation(id);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping("/{id}/fulfill")
    @Operation(summary = "Fulfill a reservation", description = "Marks reservation as FULFILLED and auto-checks out the book for the user")
    public ResponseEntity<?> fulfillReservation(
            @Parameter(description = "Reservation ID") @PathVariable Long id)
            throws BookException, Exception {
        ReservationDTO reservation = reservationService.fulfillReservation(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/my")
    @Operation(summary = "Get my reservations", description = "Returns the current user's reservations with optional filters")
    public ResponseEntity<PageResponse<ReservationDTO>> getMyReservations(
            @Parameter(description = "Filter by status") @RequestParam(required = false) ReservationStatus status,
            @Parameter(description = "Show only active reservations") @RequestParam(required = false) Boolean activeOnly,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "reservedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDirection) throws Exception {

        ReservationSearchRequest searchRequest = new ReservationSearchRequest();
        searchRequest.setStatus(status);
        searchRequest.setActiveOnly(activeOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<ReservationDTO> reservations = reservationService.getMyReservations(searchRequest);
        return ResponseEntity.ok(reservations);
    }


    @GetMapping
    @Operation(summary = "Search all reservations (Admin)", description = "Admin: search all reservations with filters for userId, bookId, status, pagination")
    public ResponseEntity<PageResponse<ReservationDTO>> searchReservations(
            @Parameter(description = "Filter by user ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "Filter by book ID") @RequestParam(required = false) Long bookId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) ReservationStatus status,
            @Parameter(description = "Show only active") @RequestParam(required = false) Boolean activeOnly,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "reservedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String sortDirection) {

        ReservationSearchRequest searchRequest = new ReservationSearchRequest();
        searchRequest.setBookId(bookId);
        searchRequest.setStatus(status);
        searchRequest.setActiveOnly(activeOnly);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        PageResponse<ReservationDTO> reservations = reservationService.searchReservations(searchRequest);
        return ResponseEntity.ok(reservations);
    }

}
