package com.example.SupplyChainManagement.repository;


import com.example.SupplyChainManagement.model.ManuOrderItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ManuOrderItemRepository extends JpaRepository<ManuOrderItem, Long> {
	List<ManuOrderItem> findByManuOrder_OrderId(Long orderId);
	
	@Query("SELECT moi FROM ManuOrderItem moi " +
	           "JOIN moi.manuOrder mo " +
	           "WHERE mo.deliverStatus = 'Delivered' AND mo.transactionStatus = 'Paid'")
	    List<ManuOrderItem> findDeliveredItems();
}