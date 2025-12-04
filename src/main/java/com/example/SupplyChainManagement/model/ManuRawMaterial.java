package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "manu_raw_materials")
public class ManuRawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "raw_material_mid")
    private Long rawMaterialMid;

    @ManyToOne
    @JoinColumn(name = "manufacturer_rid", referencedColumnName = "manufacturer_id", nullable = false)
    private Manufacturer manufacturer;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "qty_on_hand", nullable = false)
    private int qtyOnHand;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;

    @ManyToOne
    @JoinColumn(name = "category_rrid", referencedColumnName = "category_id", nullable = false)
    private Category category;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "added_date", nullable = false)
    private LocalDate addedDate;

    // Getters and Setters
    public Long getRawMaterialMid() {
        return rawMaterialMid;
    }

    public void setRawMaterialMid(Long rawMaterialMid) {
        this.rawMaterialMid = rawMaterialMid;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public LocalDate getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(LocalDate addedDate) {
		this.addedDate = addedDate;
	}
    
}
