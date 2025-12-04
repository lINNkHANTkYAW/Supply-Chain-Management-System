package com.example.SupplyChainManagement.service;

import com.example.SupplyChainManagement.model.OrderSummary;
import com.example.SupplyChainManagement.model.ProductSales;
import com.example.SupplyChainManagement.repository.SupplierOrderRepository;
import com.example.SupplyChainManagement.repository.ManuOrderRepository;
import com.example.SupplyChainManagement.repository.ProductSalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierDashboardService {

    @Autowired
    private ProductSalesRepository productSalesRepository;

    @Autowired
    private SupplierOrderRepository orderRepository;
    
    @Autowired
    private ManuOrderRepository manuOrderRepository;

    // Get supplier stats (products sold, net profit, customer satisfaction)
    /*public Map<String, Object> getSupplierStats(Long supplierId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("productsSold", productSalesRepository.getTotalProductsSold(supplierId));
        stats.put("netProfit", productSalesRepository.getTotalRevenue(supplierId));
        stats.put("customerSatisfaction", productSalesRepository.getCustomerSatisfaction(supplierId));
        return stats;
    }*/
    
    public Map<String, Object> getSupplierStats(Long supplierId) {
        Map<String, Object> stats = new HashMap<>();
        Integer productsSold = productSalesRepository.getTotalProductsSold(supplierId);
        stats.put("productsSold", productsSold != null ? productsSold : 0); // Handle null for productsSold
        Double netProfit = productSalesRepository.getTotalRevenue(supplierId); // Use Double to handle null
        stats.put("netProfit", netProfit != null ? netProfit : 0.0); // Default to 0.0 if null
        int pendingOrders = manuOrderRepository.countByStatusAndSupplierId("Pending", supplierId);
        stats.put("pendingOrders", pendingOrders);
        System.out.println("Stats for supplierId " + supplierId + ": " + stats); // Debug log
        return stats;
    }

    public List<ProductSales> getProductSalesBySupplierId(Long id) {
        return productSalesRepository.findBySupplierSupplierId(id);
    }

    public List<OrderSummary> getOrdersBySupplierId(Long id) {
        return orderRepository.findBySupplierSupplierId(id);
    }

    public List<Integer> getMonthlyProductSalesData(Long id) {
        List<Object[]> results = productSalesRepository.getMonthlySalesData(id);
        return results.stream()
                .map(result -> ((Number) result[1]).intValue())
                .collect(Collectors.toList());
    } 

    public List<String> getMonthlyProductSalesLabels(Long id) {
        List<Object[]> results = productSalesRepository.getMonthlySalesLabels(id);
        return results.stream()
                .map(result -> (String) result[0]) // Extract MONTHNAME
                .collect(Collectors.toList());
    } 
    
    public Map<String, Object> getMonthlySalesData(Long supplierId) {
        List<Object[]> results = productSalesRepository.getMonthlySalesData(supplierId);
        Map<String, Object> monthlySales = new HashMap<>();

        // Initialize labels and values arrays
        String[] labels = new String[12];
        int[] values = new int[12];

        // Default labels for all months
        String[] allMonths = {"January", "February", "March", "April", "May", "June", 
                              "July", "August", "September", "October", "November", "December"};

        // Initialize values to 0
        for (int i = 0; i < 12; i++) {
            labels[i] = allMonths[i];
            values[i] = 0;
        }

        // Populate values with actual data
        for (Object[] result : results) {
            String month = (String) result[0]; // Month name (e.g., "January")
            int totalQuantity = ((Number) result[1]).intValue(); // Total quantity sold

            // Find the index of the month in the allMonths array
            for (int i = 0; i < allMonths.length; i++) {
                if (allMonths[i].equalsIgnoreCase(month)) {
                    values[i] = totalQuantity;
                    break;
                }
            }
        }

        // Add labels and values to the response
        monthlySales.put("labels", labels);
        monthlySales.put("values", values);

        return monthlySales;
    }

 // Get order completion rate for a specific supplier
    public Map<String, Integer> getOrderCompletionRate(Long supplierId) {
        List<Object[]> results = manuOrderRepository.getOrderCompletionRate(supplierId);
        Map<String, Integer> completionRate = new HashMap<>();

        // Initialize with default values
        completionRate.put("Completed", 0);
        completionRate.put("Pending", 0);

        // Update with actual data
        for (Object[] result : results) {
            String status = (String) result[0]; // Order status ("Completed" or "Pending")
            Integer count = ((Number) result[1]).intValue(); // Number of orders with this status
            completionRate.put(status, count);
        }

        return completionRate;
    }
}