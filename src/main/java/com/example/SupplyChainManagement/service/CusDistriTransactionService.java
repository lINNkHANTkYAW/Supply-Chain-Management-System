package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.CusDistriTransaction;
import com.example.SupplyChainManagement.repository.CusDistriTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CusDistriTransactionService {

    @Autowired
    private CusDistriTransactionRepository transactionRepository;

    public List<CusDistriTransaction> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findByCustomer_CustomerId(customerId);
    }
}
