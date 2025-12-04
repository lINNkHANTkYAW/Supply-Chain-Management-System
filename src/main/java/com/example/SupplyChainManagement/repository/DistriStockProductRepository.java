package com.example.SupplyChainManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SupplyChainManagement.model.DistriStockProduct;

import java.util.List;

public interface DistriStockProductRepository extends JpaRepository<DistriStockProduct, Long> {
    List<DistriStockProduct> findByDistributorId(Long distributorId);
}