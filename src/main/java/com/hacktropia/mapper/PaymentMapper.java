package com.hacktropia.mapper;

import com.hacktropia.modal.Payment;
import com.hacktropia.payload.dto.PaymentDTO;
import org.springframework.stereotype.Component;

import java.beans.ParameterDescriptor;

@Component
public class PaymentMapper {

    public PaymentDTO toDTO(Payment payment){
        if(payment==null){
            return null;
        }
        PaymentDTO dto=new PaymentDTO();
        dto.setId(payment.getId());

        if(payment.getUser()!=null){
            dto.setUserId(payment.getUser().getId());
            dto.setUserName(payment.getUser().getFullName());
            dto.setUserEmail(payment.getUser().getEmail());
        }

//        if(payment.getBookLoan()!=null){
//            dto.setBookLoanId(payment.getBookLoan().getId());
//        }
//
        if(payment.getSubscription()!=null){
            dto.setSubscriptionId(payment.getSubscription().getId());
        }

        dto.setPaymentType(payment.getPaymentType());
        dto.setStatus(payment.getStatus());
        dto.setGateway(payment.getGateway());
        dto.setAmount(payment.getAmount());
        dto.setTransactionId(payment.getTransactionId());
        dto.setGatewayPaymentId(payment.getGatewayPaymentId());
        dto.setGatewayOrderId(payment.getGatewayOderId());
        dto.setGatewaySignature(payment.getGatewaySignature());
        dto.setDescription(payment.getDescription());
        dto.setFailureReason(payment.getFailureReason());
        dto.setInitiatedAt(payment.getInitiatedAt());
        dto.setCompletedAt(payment.getCompletedAt());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());

        return dto;
    }
}
