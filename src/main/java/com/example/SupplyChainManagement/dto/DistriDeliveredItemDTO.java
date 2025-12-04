package com.example.SupplyChainManagement.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class DistriDeliveredItemDTO {
    private Long id;
    private String name;
    private Integer quantity;
    private BigDecimal cost;
    private Date addedDate;

    public DistriDeliveredItemDTO(Long id, String name, Integer quantity, BigDecimal cost, Date addedDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.cost = cost;
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
    public Date getAddedDate() { return addedDate; }
    public void setAddedDate(Date addedDate) { this.addedDate = addedDate; }
}