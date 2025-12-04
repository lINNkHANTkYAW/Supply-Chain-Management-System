package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.InvoiceItem;
import com.example.SupplyChainManagement.model.ManuInvoiceItem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ManuInvoiceItemRepository extends JpaRepository<ManuInvoiceItem, Long> {
}