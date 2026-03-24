package com.hacktropia.mapper;

import com.hacktropia.exception.SubscriptionException;
import com.hacktropia.modal.Subscription;
import com.hacktropia.modal.SubscriptionPlan;
import com.hacktropia.modal.Users;
import com.hacktropia.payload.dto.SubscriptionDTO;
import com.hacktropia.repository.SubscriptionPlanRepository;
import com.hacktropia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SubscriptionMapper {

    private final UserRepository userRepository;
    private final SubscriptionPlanRepository planRepository;

    public SubscriptionDTO toDTO(Subscription subscription){
        if(subscription==null){
            return null;
        }

        SubscriptionDTO dto= new SubscriptionDTO();
        dto.setId(subscription.getId());

        if(subscription.getUsers() != null){
            dto.setUserId(subscription.getUsers().getId());
            dto.setUserName(subscription.getUsers().getEmail());
            dto.setUserEmail(subscription.getUsers().getEmail());
        }

        if(subscription.getPlan()!=null){
            dto.setPlanId(subscription.getPlan().getId());
        }
        dto.setPlanName(subscription.getPlanName());
        dto.setPlanCode(subscription.getPlanCode());
        dto.setPrice(subscription.getPrice());
        dto.setStartDate(subscription.getStartDate());
        dto.setEndDate(subscription.getEndDate());
        dto.setIsActive(subscription.getIsActive());
        dto.setMaxBooksAllowed(subscription.getMaxBooksAllowed());
        dto.setMaxDaysPerBook(subscription.getMaxDaysPerBook());
        dto.setAutoRenew(subscription.getAutoRenew());
        dto.setCancelledAt(subscription.getCancelledAt());
        dto.setCancellationReason(subscription.getCancellationReason());
        dto.setNotes(subscription.getNotes());
        dto.setCreatedAt(subscription.getCreatedAt());
        dto.setUpdatedAt(subscription.getUpdatedAt());
        dto.setDaysRemaining(subscription.getDaysRemaining());
        dto.setIsValid(subscription.isValid());
        dto.setIsExpired(subscription.isExpired());

        return dto;
    }

    public Subscription toEntity(SubscriptionDTO dto, SubscriptionPlan plan, Users users) throws SubscriptionException {
        if(dto==null){
            return null;
        }

        Subscription subscription=new Subscription();
        subscription.setId(dto.getId());
        subscription.setUsers(users);
        subscription.setPlan(plan);
        subscription.setNotes(dto.getNotes());

        return subscription;

    }

    public List<SubscriptionDTO> toDTOList(List<Subscription> subscriptions){
        if(subscriptions==null){
            return null;
        }
        return subscriptions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
