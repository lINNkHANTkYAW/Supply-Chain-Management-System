package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.ManuRawMaterial;
import com.example.SupplyChainManagement.model.Manufacturer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ManuRawMaterialRepository extends JpaRepository<ManuRawMaterial, Long> {
    List<ManuRawMaterial> findByManufacturer_ManufacturerId(Long manufacturerId);
    
    Optional<ManuRawMaterial> findByManufacturerAndName(Manufacturer manufacturer, String name);
}