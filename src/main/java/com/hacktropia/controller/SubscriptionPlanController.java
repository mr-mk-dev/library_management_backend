package com.hacktropia.controller;

import com.hacktropia.payload.dto.SubscriptionPlanDTO;
import com.hacktropia.payload.response.ApiResponse;
import com.hacktropia.service.SubscriptionPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/subscription-plans")
@RestController
@RequiredArgsConstructor
@Tag(name = "Subscription Plans", description = "Manage subscription plan templates (public listing + admin CRUD)")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    @Operation(summary = "Get all subscription plans", description = "Returns all available subscription plans (public)")
    public ResponseEntity<?> getAllSubscriptionPlans() {
        List<SubscriptionPlanDTO> plans = subscriptionPlanService.getAllSubscriptionPlan();
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/admin/create")
    @Operation(summary = "Create a subscription plan (Admin)", description = "Creates a new subscription plan with pricing, duration, and book limits. Requires ADMIN role.")
    public ResponseEntity<?> createSubscriptionPlan(
            @Valid @RequestBody SubscriptionPlanDTO subscriptionPlanDTO
    ) throws Exception {
        SubscriptionPlanDTO plans = subscriptionPlanService.createSubscriptionPlan(
                subscriptionPlanDTO
        );
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/admin/{id}")
    @Operation(summary = "Update a subscription plan (Admin)", description = "Updates existing plan details. Requires ADMIN role.")
    public ResponseEntity<?> updateSubscriptionPlan(
            @RequestBody SubscriptionPlanDTO subscriptionPlanDTO,
            @Parameter(description = "Plan ID") @PathVariable long id
    ) throws Exception {
        SubscriptionPlanDTO plans = subscriptionPlanService.updateSubscriptionPlan(id,
                subscriptionPlanDTO
        );
        return ResponseEntity.ok(plans);
    }

    @DeleteMapping("/admin/{id}")
    @Operation(summary = "Delete a subscription plan (Admin)", description = "Deletes a subscription plan. Requires ADMIN role.")
    public ResponseEntity<?> deleteSubscriptionPlan(
            @Parameter(description = "Plan ID") @PathVariable long id
    ) throws Exception {
        subscriptionPlanService.deleteSubscriptionPlan(id);
        ApiResponse res = new ApiResponse("plan deleted successfully", true);
        return ResponseEntity.ok(res);
    }

}
