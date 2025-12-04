package com.example.SupplyChainManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import com.example.SupplyChainManagement.model.ManuStockProduct;

public interface ManuStockProductRepository extends JpaRepository<ManuStockProduct, Long> {

	// Find products by distributor ID
    List<ManuStockProduct> findByManufacturer_ManufacturerId(Long manufacturerId);

    // Find products by category ID
    List<ManuStockProduct> findByCategory_CategoryId(Long categoryId);

    // Search products by name (case insensitive)
    List<ManuStockProduct> findByNameContainingIgnoreCase(String name);
    
    List<ManuStockProduct> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    @Query("SELECT mp FROM ManuStockProduct mp WHERE mp.manufacturer.user.userId = :userId")
    List<ManuStockProduct> findByUser_UserId(Long userId);

	Optional<ManuStockProduct> findByStockMid(Long stockMid);
}
