package com.example.SupplyChainManagement.dto;

import java.time.LocalDate;

public class ManuInventoryItemUpdateDTO {
    private String name;
    private Long categoryId; // New field
    private int quantity;
    private double cost;
    private double perUnitCost;
    private LocalDate addedDate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public double getPerUnitCost() {
		return perUnitCost;
	}
	public void setPerUnitCost(double perUnitCost) {
		this.perUnitCost = perUnitCost;
	}
	public LocalDate getAddedDate() {
		return addedDate;
	}
	public void setAddedDate(LocalDate addedDate) {
		this.addedDate = addedDate;
	}

    // Getters and setters
    
}