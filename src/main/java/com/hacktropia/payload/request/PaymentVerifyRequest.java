package com.hacktropia.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.authenticator.SavedRequest;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyRequest {

    private String razorpayPaymentId;

    private String stripePaymentIntentId;
    private String stripePaymentIntentStatus;
}
