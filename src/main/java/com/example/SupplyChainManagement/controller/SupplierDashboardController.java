package com.example.SupplyChainManagement.controller;

import com.example.SupplyChainManagement.model.OrderSummary;
import com.example.SupplyChainManagement.model.ProductSales;
import com.example.SupplyChainManagement.model.Supplier;
import com.example.SupplyChainManagement.model.User;
import com.example.SupplyChainManagement.repository.SupplierRepository;
import com.example.SupplyChainManagement.service.SupplierDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/supplierdashboard")
public class SupplierDashboardController {

    @Autowired
    private SupplierDashboardService supplierDashboardService;

    @Autowired
    private SupplierRepository supplierRepository;

    @GetMapping
    public String showDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        Long userId = user.getUserId();
        Optional<Supplier> supplierOptional = supplierRepository.findByUserUserId(userId);
        if (supplierOptional.isEmpty()) {
            return "redirect:/supplier-dashboard";
        }

        Supplier supplier = supplierOptional.get();
        model.addAttribute("user", user);
        model.addAttribute("supplier", supplier);

        Map<String, Object> stats = supplierDashboardService.getSupplierStats(supplier.getSupplierId());
        List<ProductSales> productSales = supplierDashboardService.getProductSalesBySupplierId(supplier.getSupplierId());
        List<OrderSummary> orders = supplierDashboardService.getOrdersBySupplierId(supplier.getSupplierId());

        List<Integer> productSalesData = supplierDashboardService.getMonthlyProductSalesData(supplier.getSupplierId());
        List<String> productSalesLabels = supplierDashboardService.getMonthlyProductSalesLabels(supplier.getSupplierId());
        Map<String, Integer> orderCompletionRate = supplierDashboardService.getOrderCompletionRate(supplier.getSupplierId());

        model.addAttribute("stats", stats);
        model.addAttribute("productSales", productSales);
        model.addAttribute("orders", orders);
        model.addAttribute("productSalesData", productSalesData);
        model.addAttribute("productSalesLabels", productSalesLabels);
        model.addAttribute("orderCompletionRate", orderCompletionRate);
        
        System.out.println("Product Sales Data: " + productSalesData);
        System.out.println("Product Sales Labels: " + productSalesLabels);
        System.out.println("Order Completion Rate: " + orderCompletionRate);

        return "supplier-dashboard";
    }

    @GetMapping("/stats/{supplierId}")
    public ResponseEntity<Map<String, Object>> getSupplierStats(@PathVariable Long supplierId) {
        Map<String, Object> stats = supplierDashboardService.getSupplierStats(supplierId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders")
    @ResponseBody
    public List<OrderSummary> getOrders(@RequestParam Long id) {
        return supplierDashboardService.getOrdersBySupplierId(id);
    }

    @GetMapping("/api/data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestParam Long id) {
        Map<String, Object> data = supplierDashboardService.getSupplierStats(id);
        return ResponseEntity.ok(data);
    }
    
    @GetMapping("/completion-rate/{supplierId}")
    public ResponseEntity<Map<String, Integer>> getOrderCompletionRate(@PathVariable Long supplierId) {
        Map<String, Integer> completionRate = supplierDashboardService.getOrderCompletionRate(supplierId);
        return ResponseEntity.ok(completionRate);
    }
    
 // Get monthly sales data for a specific supplier
    @GetMapping("/monthly-sales/{supplierId}")
    public ResponseEntity<Map<String, Object>> getMonthlySalesData(@PathVariable Long supplierId) {
        Map<String, Object> monthlySales = supplierDashboardService.getMonthlySalesData(supplierId);
        return ResponseEntity.ok(monthlySales);
    }
} 