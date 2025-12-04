package com.example.SupplyChainManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.SupplyChainManagement.model.ManuProduct;
import com.example.SupplyChainManagement.model.SupplierRawMaterial;

@Repository
public interface SupplierMaterialRepository extends JpaRepository<SupplierRawMaterial, Long> {

    // Remove the static method and make it a proper JPA repository method
    List<SupplierRawMaterial> findBySupplierSupplierId(Long supplierId);  // Note the camelCase convention
    
    Optional<SupplierRawMaterial> findByRawMaterialSid(Long rawMaterialSid);
    
    @Query("SELECT srm FROM SupplierRawMaterial srm WHERE srm.supplier.user.userId = :userId")
    List<SupplierRawMaterial> findByUserUserId(Long userId);  // Changed to camelCase
    
    List<SupplierRawMaterial> findByCategoryCategoryId(Long categoryId);  // Changed to camelCase

    List<SupplierRawMaterial> findByNameContainingIgnoreCase(String name);
    
    List<SupplierRawMaterial> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

 // Find products by category ID
    List<SupplierRawMaterial> findByCategory_CategoryId(Long categoryId);
    
    List<SupplierRawMaterial> findBySupplier_SupplierId(Long supplierId);
}