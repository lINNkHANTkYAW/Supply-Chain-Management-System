package com.example.SupplyChainManagement.dto;

import java.math.BigDecimal;

public class ManuProductUpdateDTO {
    private String name;
    private Integer stockQuantity;
    private BigDecimal costPerUnit;
    private BigDecimal Cost;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(BigDecimal costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public BigDecimal getCost() {
        return Cost;
    }

    public void setCost(BigDecimal cost) {
        Cost = cost;
    }
}