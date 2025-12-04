package com.example.SupplyChainManagement.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.SupplyChainManagement.dto.ManuMonthlySalesDTO;
import com.example.SupplyChainManagement.dto.ManuOrderCompletionDTO;
import com.example.SupplyChainManagement.dto.ManuRevenueDistributionDTO;
import com.example.SupplyChainManagement.repository.ManuOrderRepository;
import com.example.SupplyChainManagement.repository.SupplierRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManuAnalyticsService {

    @Autowired
    private ManuOrderRepository ordersRepository;

    @Autowired
    private SupplierRepository suppliersRepository;

    public List<ManuMonthlySalesDTO> getMonthlySales(Long manufacturerId) {
        List<Object[]> results = ordersRepository.findMonthlySales(manufacturerId);
        List<ManuMonthlySalesDTO> sales = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Double salesAmount = ((Number) result[1]).doubleValue();
            sales.add(new ManuMonthlySalesDTO(months[month - 1], salesAmount));
        }
        return sales;
    }

    public List<ManuRevenueDistributionDTO> getRevenueDistribution(Long manufacturerId) {
        List<Object[]> results = suppliersRepository.findRevenueBySupplier(manufacturerId);
        System.out.println("Revenue Results Size: " + results.size()); // Debug
        List<ManuRevenueDistributionDTO> distribution = new ArrayList<>();
        for (Object[] result : results) {
            String supplierName = (String) result[0];
            Double revenue = ((Number) result[1]).doubleValue();
            System.out.println("Supplier: " + supplierName + ", Revenue: " + revenue); // Debug
            distribution.add(new ManuRevenueDistributionDTO(supplierName, revenue));
        }
        return distribution;
    }

    public List<ManuRevenueDistributionDTO> getTopSuppliers(Long manufacturerId) {
        return getRevenueDistribution(manufacturerId);
    }

    public List<ManuOrderCompletionDTO> getOrderCompletion(Long manufacturerId) {
        List<ManuOrderCompletionDTO> completion = new ArrayList<>();
        completion.add(new ManuOrderCompletionDTO("Completed", ordersRepository.countByStatus(manufacturerId, "Completed")));
        completion.add(new ManuOrderCompletionDTO("Pending", ordersRepository.countByStatus(manufacturerId, "Pending")));
        return completion;
    }
}
