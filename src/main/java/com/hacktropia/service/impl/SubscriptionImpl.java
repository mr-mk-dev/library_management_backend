package com.hacktropia.service.impl;

import com.hacktropia.domain.PaymentGateway;
import com.hacktropia.domain.PaymentType;
import com.hacktropia.exception.SubscriptionException;
import com.hacktropia.mapper.SubscriptionMapper;
import com.hacktropia.modal.Subscription;
import com.hacktropia.modal.SubscriptionPlan;
import com.hacktropia.modal.Users;
import com.hacktropia.payload.dto.SubscriptionDTO;
import com.hacktropia.payload.request.PaymentInitiateRequest;
import com.hacktropia.payload.response.PaymentInitiateResponse;
import com.hacktropia.repository.SubscriptionPlanRepository;
import com.hacktropia.repository.SubscriptionRepository;
import com.hacktropia.service.PaymentService;
import com.hacktropia.service.SubscriptionService;
import com.hacktropia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserService userService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentService paymentService;

    @Override
    public PaymentInitiateResponse subscribe(SubscriptionDTO subscriptionDTO) throws Exception {

        Users users = userService.getCurrentUser();
        SubscriptionPlan plan=subscriptionPlanRepository
                .findById(subscriptionDTO.getPlanId()).orElseThrow(
                        ()-> new Exception("Plan not found!")
                );

        Subscription subscription=subscriptionMapper.toEntity(subscriptionDTO,plan, users);
        subscription.initializeFromPlan();
        subscription.setIsActive(false);
        Subscription savedSubscription=subscriptionRepository.save(subscription);

        PaymentInitiateRequest paymentInitiateRequest=PaymentInitiateRequest
                .builder()
                .userId(users.getId())
                .subscriptionId(subscription.getId())
                .paymentType(PaymentType.MEMBERSHIP)
                .gateway(PaymentGateway.RAZORPAY)
                .amount(subscription.getPrice())
                .description("Library Subscription - " + plan.getName())
                .build();
        return paymentService.initiatePayment(paymentInitiateRequest);
    }

    @Override
    public SubscriptionDTO getUsersActiveSubscription(Long UserId) throws Exception {
        Users users =userService.getCurrentUser();
        Subscription subscription=subscriptionRepository
                .findActiveSubscriptionByUserId(users.getId(), LocalDate.now())
                .orElseThrow(()-> new SubscriptionException("no active subscription found!"));
        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO cancelSubscription(Long subscriptionId, String reason) throws SubscriptionException {
        Subscription subscription=subscriptionRepository.findById(subscriptionId)
                .orElseThrow(()-> new SubscriptionException("Subscription not found with ID: "+ subscriptionId));
        if(!subscription.getIsActive()){
            throw new SubscriptionException("Subscription is already inactive");
        }

        subscription.setIsActive(false);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setCancellationReason(reason!=null? reason: "Cancelled by user");

        subscription=subscriptionRepository.save(subscription);

        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public SubscriptionDTO activateSubscription(Long subscriptionId, Long paymentId) throws SubscriptionException {
        Subscription subscription=subscriptionRepository.findById(subscriptionId)
                .orElseThrow(
                        ()-> new SubscriptionException("subscription not found by id!")
                );
        subscription.setIsActive(true);
        subscription=subscriptionRepository.save(subscription);
        return subscriptionMapper.toDTO(subscription);
    }

    @Override
    public List<SubscriptionDTO> getAllSubscriptions(Pageable pageable) {
        List<Subscription> subscriptions=subscriptionRepository.findAll();
        return subscriptionMapper.toDTOList(subscriptions);

    }

    @Override
    public void deactivateExpiredSubscriptions() throws Exception {
        List<Subscription> expiredSubscriptions=subscriptionRepository
                .findExpiredActiveSubscriptions(LocalDate.now());

        for(Subscription subscription : expiredSubscriptions){
            subscription.setIsActive(false);
            subscriptionRepository.save(subscription);
        }
    }
}
