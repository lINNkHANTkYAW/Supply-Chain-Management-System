package com.example.SupplyChainManagement.dto;

public class DistriRevenueDistributionDTO {
    private String supplierName;
    private Double revenue;

    public DistriRevenueDistributionDTO(String supplierName, Double revenue) {
        this.supplierName = supplierName;
        this.revenue = revenue;
    }

    // Getters and Setters
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Double getRevenue() { return revenue; }
    public void setRevenue(Double revenue) { this.revenue = revenue; }
}