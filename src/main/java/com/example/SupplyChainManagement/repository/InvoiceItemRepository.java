package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}