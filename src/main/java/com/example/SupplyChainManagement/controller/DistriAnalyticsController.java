/* package com.example.SupplyChainManagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SupplyChainManagement.dto.DistriMonthlySalesDTO;
import com.example.SupplyChainManagement.dto.DistriOrderCompletionDTO;
import com.example.SupplyChainManagement.dto.DistriRevenueDistributionDTO;
import com.example.SupplyChainManagement.service.DistriAnalyticsService;

@RestController
@RequestMapping("/api/distributor/analytics")
public class DistriAnalyticsController {

    @Autowired
    private DistriAnalyticsService analyticsService;

    @GetMapping("/monthly-sales")
    public List<DistriMonthlySalesDTO> getMonthlySales() {
        return analyticsService.getMonthlySales();
    }

    @GetMapping("/revenue-distribution")
    public List<DistriRevenueDistributionDTO> getRevenueDistribution() {
        return analyticsService.getRevenueDistribution();
    }

    @GetMapping("/top-suppliers")
    public List<DistriRevenueDistributionDTO> getTopSuppliers() { // Changed to RevenueDistributionDTO
        return analyticsService.getTopSuppliers();
    }

    @GetMapping("/order-completion")
    public List<DistriOrderCompletionDTO> getOrderCompletion() {
        return analyticsService.getOrderCompletion();
    }
} */
