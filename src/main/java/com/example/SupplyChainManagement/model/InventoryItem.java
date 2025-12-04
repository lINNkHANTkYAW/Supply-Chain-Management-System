package com.example.SupplyChainManagement.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_item")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String date;
    private int quantity;
    private BigDecimal cost;
    private BigDecimal perUnitCost;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnore  // 🔴 Prevents sending the whole supplier object in JSON response
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "item_type_id", nullable = false)
    private ItemType itemType;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false) // 🔹 Add foreign key relationship
    private Category category;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getPerUnitCost() {
		return perUnitCost;
	}

	public void setPerUnitCost(BigDecimal perUnitCost) {
		this.perUnitCost = perUnitCost;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}
	public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

	
 
}

