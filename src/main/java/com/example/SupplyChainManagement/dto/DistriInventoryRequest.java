package com.example.SupplyChainManagement.dto;


import lombok.Data;

@Data
public class DistriInventoryRequest {
    private String itemName;
    private Integer itemQuantity;
    private Double itemCost;
    private Double itemCostPerUnit;
    private String itemAddedDate;
    private Long category;
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Integer getItemQuantity() {
		return itemQuantity;
	}
	public void setItemQuantity(Integer itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	public Double getItemCost() {
		return itemCost;
	}
	public void setItemCost(Double itemCost) {
		this.itemCost = itemCost;
	}
	public Double getItemCostPerUnit() {
		return itemCostPerUnit;
	}
	public void setItemCostPerUnit(Double itemCostPerUnit) {
		this.itemCostPerUnit = itemCostPerUnit;
	}
	public String getItemAddedDate() {
		return itemAddedDate;
	}
	public void setItemAddedDate(String itemAddedDate) {
		this.itemAddedDate = itemAddedDate;
	}
	public Long getCategory() {
		return category;
	}
	public void setCategory(Long category) {
		this.category = category;
	}
    
    
}