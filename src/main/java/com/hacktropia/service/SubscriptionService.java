package com.hacktropia.service;

import com.hacktropia.exception.SubscriptionException;
import com.hacktropia.payload.dto.SubscriptionDTO;
import com.hacktropia.payload.response.PaymentInitiateResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {

    PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO) throws Exception;
    SubscriptionDTO getUsersActiveSubscription(Long UserId) throws Exception;
    SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) throws SubscriptionException;
    SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException;
    List<SubscriptionDTO> getAllSubscriptions(Pageable pageable);

    void deactivateExpiredSubscriptions() throws Exception;


}
