package com.example.SupplyChainManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SupplyChainManagement.model.Manufacturer;
import com.example.SupplyChainManagement.model.User;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
    Optional<Manufacturer> findByUser(User user);
    
    Optional<Manufacturer> findByUser_UserId(Long userId);
    
}

