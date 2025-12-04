package com.example.SupplyChainManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;
import com.example.SupplyChainManagement.model.User;

@Repository
public interface DistriProductRepository extends JpaRepository<DistriProduct, Long> {

    // Find products by distributor ID
    List<DistriProduct> findByDistributor_DistributorId(Long distributorId);

    // Find products by category ID
    List<DistriProduct> findByCategory_CategoryId(Long categoryId);

    // Search products by name (case insensitive)
    List<DistriProduct> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM DistriProduct p JOIN CusOrderItem oi ON p.productId = oi.distriProduct.productId " +
            "GROUP BY p ORDER BY COUNT(oi.orderItemId) DESC")
    List<DistriProduct> findTopTrendingProducts();
    
    List<DistriProduct> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    
    List<DistriProduct> findTop5ByOrderByStockQuantityDesc(); // Fetch top 5 trending

    List<DistriProduct> findTop8ByOrderByProductIdDesc(); // Fetch top 8 newly arrived
    
    @Query("SELECT dp FROM DistriProduct dp WHERE dp.distributor.user.userId = :userId")
    List<DistriProduct> findByUser_UserId(Long userId);

	Optional<DistriProduct> findByProductId(Long manuProductId);
}


