package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "manu_inventory_item")
public class ManuInventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Name is required")
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(name = "cost", nullable = false)
    @NotNull(message = "Cost is required")
    @Min(value = 0, message = "Cost must be at least 0")
    private Double cost;

    @Column(name = "per_unit_cost", nullable = false)
    private Double perUnitCost;

    @Column(name = "added_date", nullable = false)
    @NotNull(message = "Added date is required")
    private LocalDate addedDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculatePerUnitCost(); // Dynamically calculate perUnitCost when quantity changes
    }
    public Double getCost() { return cost; }
    public void setCost(Double cost) {
        this.cost = cost;
        calculatePerUnitCost(); // Dynamically calculate perUnitCost when cost changes
    }
    public Double getPerUnitCost() { return perUnitCost; }
    public void setPerUnitCost(Double perUnitCost) { this.perUnitCost = perUnitCost; }
    public LocalDate getAddedDate() { return addedDate; }
    public void setAddedDate(LocalDate addedDate) { this.addedDate = addedDate; }

    private void calculatePerUnitCost() {
        if (quantity != null && quantity > 0 && cost != null) {
            this.perUnitCost = cost / quantity;
        } else {
            this.perUnitCost = 0.0; // Default to 0 if calculation is not possible
        }
    }
	public Manufacturer getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}
}