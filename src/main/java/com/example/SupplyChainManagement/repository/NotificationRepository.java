package com.example.SupplyChainManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.SupplyChainManagement.model.ItemType;

@Repository
public interface NotificationRepository extends JpaRepository<ItemType, Long> {
}
