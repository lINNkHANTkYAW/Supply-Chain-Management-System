package com.example.SupplyChainManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SupplyChainManagement.model.InventoryItem;
import com.example.SupplyChainManagement.model.ManuInventoryItem;

@Repository
public interface ManuInventoryItemRepository extends JpaRepository<ManuInventoryItem, Long> {
	List<ManuInventoryItem> findByManufacturer_ManufacturerId(Long manufacturerId);
	Optional<ManuInventoryItem> findByName(String name);
}