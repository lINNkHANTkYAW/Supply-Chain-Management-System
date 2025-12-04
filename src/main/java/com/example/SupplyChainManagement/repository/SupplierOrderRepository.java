package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.OrderSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierOrderRepository extends JpaRepository<OrderSummary, Long> {

    List<OrderSummary> findBySupplierSupplierId(Long supplierId);

    @Query("SELECT o.status, COUNT(o.id) FROM OrderSummary o WHERE o.supplier.id = :id GROUP BY o.status")
    List<Object[]> getOrderCompletionRate(Long id);
}