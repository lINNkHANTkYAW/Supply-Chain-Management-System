package com.example.SupplyChainManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByUser(User user); // Get supplier by user reference
    
    Optional<Supplier> findByUser_UserId(Long userId);
    
    Optional<Supplier> findByUserUserId(Long userId);
    
    @Query("SELECT s.companyName, SUM(oi.quantity * r.unitPrice) as revenue " +
            "FROM ManuOrder o JOIN ManuOrderItem oi ON o.orderId = oi.manuOrder.orderId " +
            "JOIN SupplierRawMaterial r ON oi.supplierRawMaterial.rawMaterialSid = r.rawMaterialSid " +
            "JOIN Supplier s ON o.supplier.supplierId = s.supplierId " +
            "WHERE o.manufacturer.manufacturerId = :manufacturerId " +
            "GROUP BY s.companyName " +
            "ORDER BY revenue DESC")
     List<Object[]> findRevenueBySupplier(Long manufacturerId);
}
