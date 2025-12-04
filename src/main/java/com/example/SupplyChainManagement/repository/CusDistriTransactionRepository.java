package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.CusDistriTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CusDistriTransactionRepository extends JpaRepository<CusDistriTransaction, Long> {
    List<CusDistriTransaction> findByCustomer_CustomerId(Long customerId);
}
