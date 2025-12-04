package com.example.SupplyChainManagement.dto;


public class ManuRevenueDistributionDTO {
    private String supplierName;
    private Double revenue;

    public ManuRevenueDistributionDTO(String supplierName, Double revenue) {
        this.supplierName = supplierName;
        this.revenue = revenue;
    }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public Double getRevenue() { return revenue; }
    public void setRevenue(Double revenue) { this.revenue = revenue; }
}