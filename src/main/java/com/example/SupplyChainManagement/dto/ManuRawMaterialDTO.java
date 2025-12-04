package com.example.SupplyChainManagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ManuRawMaterialDTO {
    private Long rawMaterialMid;
    private String name;
    private String categoryName;
    private int qtyOnHand;
    private BigDecimal unitCost;
    private BigDecimal totalCost; // New field
    private LocalDate addedDate;

    // Getters and Setters
    public Long getRawMaterialMid() {
        return rawMaterialMid;
    }

    public void setRawMaterialMid(Long rawMaterialMid) {
        this.rawMaterialMid = rawMaterialMid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }
}
