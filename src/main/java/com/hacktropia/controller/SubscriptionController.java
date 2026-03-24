package com.hacktropia.controller;

import com.hacktropia.exception.SubscriptionException;
import com.hacktropia.payload.dto.SubscriptionDTO;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.payload.response.PaymentInitiateResponse;
import com.hacktropia.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscriptions", description = "User subscription management — subscribe to plans, activate after payment, cancel, view active subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to a plan", description = "Creates an inactive subscription and initiates a RazorPay payment. Subscription activates after payment is verified.")
    public ResponseEntity<?> subscribe(
            @RequestBody SubscriptionDTO subscription
    ) throws Exception {
        PaymentInitiateResponse dto = subscriptionService.subscribe(subscription);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/active")
    @Operation(summary = "Get active subscription", description = "Returns the current user's active subscription (if any). Optionally pass a userId.")
    public ResponseEntity<?> getUsersActiveSubscription(
            @Parameter(description = "Optional user ID (defaults to current user)") @RequestParam(required = false) Long userId
    ) throws Exception {
        SubscriptionDTO dto = subscriptionService.getUsersActiveSubscription(userId);
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/admin")
    @Operation(summary = "Get all subscriptions (Admin)", description = "Returns paginated list of all subscriptions")
    public ResponseEntity<?> getAllSubscriptions() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        List<SubscriptionDTO> dtoList = subscriptionService.getAllSubscriptions(pageable);
        return ResponseEntity.ok(dtoList);
    }


    @GetMapping("/admin/deactivate-expired")
    @Operation(summary = "Deactivate expired subscriptions (Admin)", description = "Finds all subscriptions past their end date and sets isActive = false")
    public ResponseEntity<?> deactivateExpiredSubscriptions() throws Exception {
        subscriptionService.deactivateExpiredSubscriptions();
        ApiResponse res = new ApiResponse("task done!", true);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/cancel/{subscriptionId}")
    @Operation(summary = "Cancel a subscription", description = "Deactivates a subscription with an optional cancellation reason")
    public ResponseEntity<?> cancelSubscription(
            @Parameter(description = "Subscription ID") @PathVariable Long subscriptionId,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String reason) throws SubscriptionException {
        SubscriptionDTO subscription = subscriptionService
                .cancelSubscription(subscriptionId, reason);
        return ResponseEntity.ok(subscription);
    }

    @PostMapping("/activate")
    @Operation(summary = "Activate a subscription", description = "Activates a subscription after payment confirmation")
    public ResponseEntity<?> activateSubscription(
            @Parameter(description = "Subscription ID") @RequestParam Long subscriptionId,
            @Parameter(description = "Payment ID") @RequestParam Long paymentId) throws SubscriptionException {

        SubscriptionDTO subscription = subscriptionService
                .activateSubscription(subscriptionId, paymentId);
        return ResponseEntity.ok(subscription);
    }

}
