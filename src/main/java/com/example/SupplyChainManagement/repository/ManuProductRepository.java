package com.example.SupplyChainManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.User;


@Repository
public interface ManuProductRepository extends JpaRepository<ManuProduct, Long> {

    // Find products by distributor ID
    List<ManuProduct> findByManufacturer_ManufacturerId(Long manufacturerId);

    // Find products by category ID
    List<ManuProduct> findByCategory_CategoryId(Long categoryId);

    // Search products by name (case insensitive)
    List<ManuProduct> findByNameContainingIgnoreCase(String name);
    
    /* @Query("SELECT mp FROM ManuProduct mp JOIN ManuOrderItem moi ON mp.productMid = moi.manuProduct.productMid " +
            "GROUP BY mp ORDER BY COUNT(moi.orderItemId) DESC")
    List<ManuProduct> findTopTrendingProducts(); */

    
    List<ManuProduct> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    List<ManuProduct> findTop5ByOrderByStockQuantityDesc(); // Fetch top 5 trending

    List<ManuProduct> findTop8ByOrderByProductMidDesc(); // Fetch top 8 newly arrived
    
    @Query("SELECT mp FROM ManuProduct mp WHERE mp.manufacturer.user.userId = :userId")
    List<ManuProduct> findByUser_UserId(Long userId);

	Optional<ManuProduct> findByProductMid(Long productMid);

	
}


