package com.example.SupplyChainManagement.dto;

import java.math.BigDecimal;

public class ItemDTO {
    private Long itemId;
    private String productName;
    private String categoryName;
    private String imageUrl;
    private int quantity;
    private int rating;
    private String distributorName;
    private BigDecimal price;
    private String description;

    // Constructor
    public ItemDTO(Long itemId, String productName, String categoryName, String imageUrl, int quantity, int rating, String distributorName, BigDecimal price, String description) {
        this.itemId = itemId;
        this.productName = productName;
        this.categoryName = categoryName;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.rating = rating;
        this.distributorName = distributorName;
        this.price = price;
        this.description = description;
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

	// Getters and Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDistributorName() {
        return distributorName;
    }

    public void setDistributorName(String distributorName) {
        this.distributorName = distributorName;
    }
}