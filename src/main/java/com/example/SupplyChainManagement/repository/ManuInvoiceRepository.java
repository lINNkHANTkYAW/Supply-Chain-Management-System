package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.Invoice;
import com.example.SupplyChainManagement.model.ManuInvoice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ManuInvoiceRepository extends JpaRepository<ManuInvoice, Long> {
}