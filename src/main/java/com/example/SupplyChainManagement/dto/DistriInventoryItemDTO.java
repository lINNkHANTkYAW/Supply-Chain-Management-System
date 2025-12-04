package com.example.SupplyChainManagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DistriInventoryItemDTO {
    private Long id;
    private String name;
    private Integer quantity;
    private BigDecimal cost;
    private BigDecimal costPerUnit;
    private LocalDate addedDate;

    public DistriInventoryItemDTO(Long id, String name, Integer quantity, BigDecimal cost, BigDecimal costPerUnit, LocalDate addedDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.cost = cost;
        this.costPerUnit = costPerUnit;
        this.addedDate = addedDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
    public BigDecimal getCostPerUnit() { return costPerUnit; }
    public void setCostPerUnit(BigDecimal costPerUnit) { this.costPerUnit = costPerUnit; }
    public LocalDate getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDate addedDate) { this.addedDate = addedDate; }
}
