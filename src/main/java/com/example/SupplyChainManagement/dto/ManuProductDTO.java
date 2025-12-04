package com.example.SupplyChainManagement.dto;

import com.example.SupplyChainManagement.model.ManuProduct;
import java.math.BigDecimal;

public class ManuProductDTO {
    private Long productMid;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer stockQuantity;
    private String categoryName; // Derived from the Category entity
    private BigDecimal costPerUnit; // Calculated field
    private String image;

    // Getters and Setters
    public Long getProductMid() {
        return productMid;
    }

    public void setProductMid(Long productMid) {
        this.productMid = productMid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(BigDecimal costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}