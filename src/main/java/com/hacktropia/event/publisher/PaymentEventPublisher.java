package com.hacktropia.event.publisher;

import com.hacktropia.modal.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    public void publishPaymentSuccessEvent(Payment payment){
        applicationEventPublisher.publishEvent(payment);
    }
}
