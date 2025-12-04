package com.example.SupplyChainManagement.controller;


import com.example.SupplyChainManagement.dto.InvoiceRequest;
import com.example.SupplyChainManagement.dto.InvoiceResponse;
import com.example.SupplyChainManagement.model.Invoice;
import com.example.SupplyChainManagement.model.InvoiceItem;
import com.example.SupplyChainManagement.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    InvoiceService invoiceService;

    /* @PostMapping
    public Invoice createInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        return invoiceService.createInvoice(invoiceRequest);
    } */
    
    @PostMapping
    public InvoiceResponse createInvoice(@RequestBody InvoiceRequest invoiceRequest) {
        Invoice invoice = invoiceService.createInvoice(invoiceRequest);
        return invoiceService.mapToInvoiceResponse(invoice);
    }

    

    /* @GetMapping("/distributor-products")
    public List<DistriProduct> getDistributorProducts(@RequestParam Long distributorId) {
        return invoiceService.getProductsByDistributor(distributorId);
    }

    @PostMapping("/create-invoice")
    public String createInvoice(@RequestBody InvoiceRequest invoice) {
        return invoiceService.createInvoice(invoice);
    } */
    
}

   
