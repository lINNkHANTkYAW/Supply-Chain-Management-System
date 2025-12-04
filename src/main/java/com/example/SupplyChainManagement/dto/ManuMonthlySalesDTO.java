package com.example.SupplyChainManagement.dto;

public class ManuMonthlySalesDTO {
    private String month;
    private Double sales;

    public ManuMonthlySalesDTO(String month, Double sales) {
        this.month = month;
        this.sales = sales;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public Double getSales() { return sales; }
    public void setSales(Double sales) { this.sales = sales; }
}
