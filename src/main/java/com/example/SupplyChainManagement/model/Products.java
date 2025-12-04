package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_ssid")
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "supplier_pid", referencedColumnName = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity = 0;

    @ManyToOne
    @JoinColumn(name = "category_spid", referencedColumnName = "category_id", nullable = false) // Ensure column name in the database is used
    private Category category;

    // Change image field to List<String> to store multiple images
    @ElementCollection
    @CollectionTable(name = "supplier_product_images", joinColumns = @JoinColumn(name = "product_ssid"))
    @Column(name = "image")
    private List<String> images;

    @Column(name = "rating", nullable = false)
    private int rating = 0;

    // Constructors
    public Products() {}

    public Products(Supplier supplier, String name, String description, BigDecimal price, 
                         BigDecimal cost, int stockQuantity, Category category, List<String> images) {
        this.supplier = supplier;
        this.name = name;
        this.description = description;
        this.price = price;
        this.cost = cost;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.images = images;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public Long getSupplierId() {
    	return supplier != null ? supplier.getSupplierId() : null; // Access distributorId through distributor
    }
}
