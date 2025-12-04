package com.example.SupplyChainManagement.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.SupplyChainManagement.dto.ManuMonthlySalesDTO;
import com.example.SupplyChainManagement.dto.ManuOrderCompletionDTO;
import com.example.SupplyChainManagement.dto.ManuRevenueDistributionDTO;
import com.example.SupplyChainManagement.service.ManuAnalyticsService;

import java.util.List;

@RestController
@RequestMapping("/api/manufacturer/analytics")
public class ManuAnalyticsController {

    @Autowired
    private ManuAnalyticsService analyticsService;

    @GetMapping("/monthly-sales/{manufacturerId}")
    public List<ManuMonthlySalesDTO> getMonthlySales(@PathVariable Long manufacturerId) {
        return analyticsService.getMonthlySales(manufacturerId);
    }

    @GetMapping("/revenue-distribution/{manufacturerId}")
    public List<ManuRevenueDistributionDTO> getRevenueDistribution(@PathVariable Long manufacturerId) {
        return analyticsService.getRevenueDistribution(manufacturerId);
    }

    @GetMapping("/top-suppliers/{manufacturerId}")
    public List<ManuRevenueDistributionDTO> getTopSuppliers(@PathVariable Long manufacturerId) {
        return analyticsService.getTopSuppliers(manufacturerId);
    }

    @GetMapping("/order-completion/{manufacturerId}")
    public List<ManuOrderCompletionDTO> getOrderCompletion(@PathVariable Long manufacturerId) {
        return analyticsService.getOrderCompletion(manufacturerId);
    }
}
