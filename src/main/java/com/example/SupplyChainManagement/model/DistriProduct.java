package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "distri_products")
public class DistriProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_did")
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "distributor_pid", referencedColumnName = "distributor_id", nullable = false)
    private Distributor distributor;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;
    
    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0;

    @ManyToOne
    @JoinColumn(name = "category_ppid", referencedColumnName = "category_id", nullable = false) // Ensure column name in the database is used
    private Category category;

    
    @Column(name = "image")
    private String image;
    
    @Column(name = "rating")
    private int rating = 0;

    public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	// Constructors
    public DistriProduct() {}

    public DistriProduct(Distributor distributor, String name, String description, BigDecimal price, 
                         BigDecimal cost, int stockQuantity, Category category, String image) {
        this.distributor = distributor;
        this.name = name;
        this.description = description;
        this.price = price;
        this.cost = cost;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.image = image;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
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

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
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
    
    public Long getDistributorId() {
    	return distributor != null ? distributor.getDistributorId() : null; // Access distributorId through distributor
    }
}
