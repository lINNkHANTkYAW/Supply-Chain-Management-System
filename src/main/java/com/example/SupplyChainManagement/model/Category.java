package com.example.SupplyChainManagement.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "image_url") // Store image URL in the database
    private String imageUrl;
    
    /*@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManuRawMaterial> manuRawMaterials;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DistriProduct> distriProducts;*/

    // Getters and Setters
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

	/*public List<ManuRawMaterial> getManuRawMaterials() {
		return manuRawMaterials;
	}

	public void setManuRawMaterials(List<ManuRawMaterial> manuRawMaterials) {
		this.manuRawMaterials = manuRawMaterials;
	}

	public List<DistriProduct> getDistriProducts() {
		return distriProducts;
	}

	public void setDistriProducts(List<DistriProduct> distriProducts) {
		this.distriProducts = distriProducts;
	} */
}

