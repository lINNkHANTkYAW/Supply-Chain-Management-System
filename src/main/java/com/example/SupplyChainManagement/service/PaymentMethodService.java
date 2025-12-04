package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.dto.PaymentMethodDTO;
import com.example.SupplyChainManagement.model.PaymentMethod;
import com.example.SupplyChainManagement.model.UserPayment;
import com.example.SupplyChainManagement.repository.PaymentMethodRepository;
import com.example.SupplyChainManagement.repository.UserPaymentRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserPaymentRepository userPaymentRepository;

    public PaymentMethodService(PaymentMethodRepository paymentMethodRepository, UserPaymentRepository userPaymentRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.userPaymentRepository = userPaymentRepository;
    }

    /** ✅ Fetch available payment methods for a distributor */
    /* public List<PaymentMethod> getPaymentMethodsByDistributor(Long distributorId) {
        return paymentMethodRepository.findByDistributorId(distributorId); 
    } */
    
    /** ✅ Fetch available payment methods for a distributor and convert to DTO */
    public List<PaymentMethodDTO> getPaymentMethodsByDistributor(Long distributorId) {
        return paymentMethodRepository.findByDistributorId(distributorId)
                .stream()
                .map(pm -> new PaymentMethodDTO(pm.getPayMethodId(), pm.getPayMethodName()))
                .collect(Collectors.toList());
    }
    
    public List<PaymentMethodDTO> getPaymentMethodsByUserId(Long userId) {
        // Fetch all UserPayment entities for the given userId
        List<UserPayment> userPayments = userPaymentRepository.findByUser_UserId(userId);

        // Map UserPayment entities to PaymentMethodDTO objects
        return userPayments.stream()
                .map(userPayment -> new PaymentMethodDTO(
                        userPayment.getPaymentMethod().getPayMethodId(),
                        userPayment.getPaymentMethod().getPayMethodName()
                ))
                .collect(Collectors.toList());
    }
}
