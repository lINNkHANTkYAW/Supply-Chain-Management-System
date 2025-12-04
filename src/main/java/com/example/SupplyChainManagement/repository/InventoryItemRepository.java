package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    // Correct method name to fetch inventory items by supplier ID
	List<InventoryItem> findBySupplier_SupplierId(Long supplierId);
	Optional<InventoryItem> findByName(String name);
}