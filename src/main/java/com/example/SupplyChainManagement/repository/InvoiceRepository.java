package com.example.SupplyChainManagement.repository;

import com.example.SupplyChainManagement.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}