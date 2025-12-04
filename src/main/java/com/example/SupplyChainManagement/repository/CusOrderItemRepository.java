package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.dto.ItemDTO;
import com.example.SupplyChainManagement.model.CusOrderItem;
import com.example.SupplyChainManagement.model.Customer;
import com.example.SupplyChainManagement.model.DistriProduct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CusOrderItemRepository extends JpaRepository<CusOrderItem, Long> {
    List<CusOrderItem> findByCusOrder_OrderId(Long orderId);
    
    @Query("SELECT c.distriProduct FROM CusOrderItem c GROUP BY c.distriProduct ORDER BY COUNT(c.distriProduct) DESC")
    List<DistriProduct> findTopTrendingProducts();
    
    @Query("SELECT new com.example.SupplyChainManagement.dto.ItemDTO(" +
            "oi.orderItemId, " + // Assuming orderItemId is the itemId
            "dp.name, " + // Product name from DistriProduct
            "c.categoryName, " + // Category name from Category
            "dp.image, " + // Image URL from DistriProduct
            "oi.quantity, " + // Quantity from CusOrderItem
            "dp.rating, " + // Rating from DistriProduct
            "d.companyName, " + // Distributor name from Distributor
            "dp.price, " +
            "dp.description) " +
            "FROM CusOrderItem oi " +
            "JOIN oi.distriProduct dp " +
            "JOIN dp.category c " +
            "JOIN dp.distributor d " + // Join with Distributor
            "WHERE c.categoryId = :categoryId")
     List<ItemDTO> findItemsByCategoryId(Long categoryId);
    
    /* List<ItemDTO> findByItemDTO_ItemId(Long itemId ); */
    
    @Query("SELECT new com.example.SupplyChainManagement.dto.ItemDTO(" +
    		"oi.orderItemId, " +
    		"dp.name, " +
    		"c.categoryName, " +
    		"dp.image, " +
    		"oi.quantity, " +
    		"dp.rating, " +
    		"d.companyName, " +
    		"dp.price, " +
    		"dp.description) " +
    		"FROM CusOrderItem oi " +
    		"JOIN oi.distriProduct dp " +
    		"JOIN dp.category c " +
    		"JOIN dp.distributor d " +
    		"WHERE oi.orderItemId = :itemId")
    ItemDTO findItemDetailsById(Long itemId);

	Optional<CusOrderItem> findByOrderItemId(Long itemId);
}




