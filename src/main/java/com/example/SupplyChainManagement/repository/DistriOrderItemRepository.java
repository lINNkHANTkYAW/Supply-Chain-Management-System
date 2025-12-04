package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.dto.DistriDeliveredItemDTO;
import com.example.SupplyChainManagement.dto.DistriInventoryItemDTO;
import com.example.SupplyChainManagement.model.DistriOrderItem;
import com.example.SupplyChainManagement.model.ManuOrderItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DistriOrderItemRepository extends JpaRepository<DistriOrderItem, Long> {
	
	@Query(value = "SELECT oi.order_item_id AS id, p.name AS name, oi.quantity AS quantity, " +
            "p.cost * oi.quantity AS cost, o.order_date AS added_date " +
            "FROM distri_order_items oi " +
            "JOIN distri_orders o ON oi.distri_order_id = o.order_id " +
            "JOIN manu_products p ON oi.manu_product_id = p.product_mid " +
            "WHERE o.distributor_id = :distributorId AND o.status = 'Completed'", 
            nativeQuery = true)
	List<DistriDeliveredItemDTO> findCompletedOrderItemsByDistributorId(@Param("distributorId") Long distributorId);
	
	List<DistriOrderItem> findByDistriOrder_Distributor_DistributorId(Long distributorId);
	
	 List<DistriOrderItem> findByDistriOrder_Distributor_DistributorIdAndDistriOrder_Status(Long distributorId, String status);
	 
	 
}