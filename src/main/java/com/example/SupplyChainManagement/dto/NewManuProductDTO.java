package com.example.SupplyChainManagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NewManuProductDTO {
    private Long productMid;
    private String name;
    private String description;
    // private BigDecimal price;
    private BigDecimal cost;
    private Integer stockQuantity;
    private Long categoryId; // Add this field
    private String categoryName; // Derived from the Category entity
    private BigDecimal costPerUnit; // Calculated field
    private String image;
    private LocalDate addedDate;

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

    /* public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    } */

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

	public LocalDate getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(LocalDate addedDate) {
		this.addedDate = addedDate;
	}
}