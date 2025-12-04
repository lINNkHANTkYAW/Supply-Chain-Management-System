package com.example.SupplyChainManagement.dto;

public class DistriMonthlySalesDTO {
    private String month;
    private Double sales;

    public DistriMonthlySalesDTO(String month, Double sales) {
        this.month = month;
        this.sales = sales;
    }

    // Getters and Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public Double getSales() { return sales; }
    public void setSales(Double sales) { this.sales = sales; }
}
