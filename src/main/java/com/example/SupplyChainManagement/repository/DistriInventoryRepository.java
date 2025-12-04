package com.example.SupplyChainManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SupplyChainManagement.model.DistriInventory;

import java.util.List;

public interface DistriInventoryRepository extends JpaRepository<DistriInventory, Long> {
    List<DistriInventory> findByDistributorId(Long distributorId);
    
    // List<DistriInventory> findByDistributorDistributorId(Long distributorId);
}