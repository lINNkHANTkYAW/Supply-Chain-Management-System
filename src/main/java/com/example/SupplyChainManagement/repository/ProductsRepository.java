package com.example.SupplyChainManagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.SupplyChainManagement.model.DistriProduct;
import com.example.SupplyChainManagement.model.Products;

public interface ProductsRepository extends JpaRepository <Products, Long>{
	
	List<Products> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
	
	@Query("SELECT p FROM DistriProduct p JOIN CusOrderItem oi ON p.productId = oi.distriProduct.productId " +
            "GROUP BY p ORDER BY COUNT(oi.orderItemId) DESC")
    List<Products> findTopTrendingProducts();
	
	List<Products> findTop5ByOrderByStockQuantityDesc(); // Fetch top 5 trending

    List<Products> findTop8ByOrderByProductIdDesc(); // Fetch top 8 newly arrived
    
    List<Products> findBySupplier_SupplierId(Long supplierId);

    // Find products by category ID
    List<Products> findByCategory_CategoryId(Long categoryId);

    // Search products by name (case insensitive)
    List<Products> findByNameContainingIgnoreCase(String name);

}
