package com.hacktropia.payload.request;

import com.hacktropia.domain.PaymentGateway;
import com.hacktropia.domain.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.authenticator.SavedRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInitiateRequest {

    @NotNull(message = "User ID is mandatory")
    private Long userId;

    private Long bookLoanId;

    @NotNull(message = "Payment type is mandatory")
    private PaymentType paymentType;

    @NotNull(message = "Payment gateway is mandatory")
    private PaymentGateway gateway;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    private Long amount;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Long fineId;
    private Long subscriptionId;

    @Size(max = 500, message = "Success URL must not exceed 500 characters")
    private String successUrl;

    @Size(max = 500,message = "Cancel URL must not exceed 500 characters")
    private String cancelUrl;
}

