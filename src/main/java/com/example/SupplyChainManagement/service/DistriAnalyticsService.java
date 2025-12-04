/* package com.example.SupplyChainManagement.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.SupplyChainManagement.dto.DistriMonthlySalesDTO;
import com.example.SupplyChainManagement.dto.DistriOrderCompletionDTO;
import com.example.SupplyChainManagement.dto.DistriRevenueDistributionDTO;
import com.example.SupplyChainManagement.repository.DistriOrderRepository;
import com.example.SupplyChainManagement.repository.DistributorRepository;

@Service
public class DistriAnalyticsService {

    @Autowired
    private DistriOrderRepository ordersRepository;

    @Autowired
    private DistributorRepository distributorsRepository;

    public List<DistriMonthlySalesDTO> getMonthlySales() {
        List<Object[]> results = ordersRepository.findMonthlySales();
        List<DistriMonthlySalesDTO> sales = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Double salesAmount = ((Number) result[1]).doubleValue();
            sales.add(new DistriMonthlySalesDTO(months[month - 1], salesAmount));
        }
        return sales;
    }

    public List<DistriRevenueDistributionDTO> getRevenueDistribution() {
        List<Object[]> results = distributorsRepository.findRevenueByDistributor();
        List<DistriRevenueDistributionDTO> distribution = new ArrayList<>();
        for (Object[] result : results) {
            String supplierName = (String) result[0];
            Double revenue = ((Number) result[1]).doubleValue();
            distribution.add(new DistriRevenueDistributionDTO(supplierName, revenue));
        }
        return distribution;
    }

    public List<DistriRevenueDistributionDTO> getTopSuppliers() {
        return getRevenueDistribution(); // Now returns the same type
    }

    public List<DistriOrderCompletionDTO> getOrderCompletion() {
        List<DistriOrderCompletionDTO> completion = new ArrayList<>();
        completion.add(new DistriOrderCompletionDTO("Completed", ordersRepository.countByStatus("Completed")));
        completion.add(new DistriOrderCompletionDTO("Pending", ordersRepository.countByStatus("Pending")));
        return completion;
    }
} */