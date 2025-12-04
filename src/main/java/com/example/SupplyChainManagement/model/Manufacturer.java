package com.example.SupplyChainManagement.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "manufacturers")
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manufacturer_id")
    private Long manufacturerId;

    @OneToOne
    @JoinColumn(name = "user_mid", referencedColumnName = "user_id", nullable = false)
    private User user;

    
    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "contact_info")
	private String contactInfo;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "bio")
    private String bio;
    
    @OneToMany(mappedBy = "manufacturer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ManuRawMaterial> rawMaterials;
    
    // Constructors, Getters, Setters...
	public Long getManufacturerId() {
		return manufacturerId;
	}
	public void setManufacturerId(Long manufacturerId) {
		this.manufacturerId = manufacturerId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getContactInfo() {
		return contactInfo;
	}
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public List<ManuRawMaterial> getRawMaterials() {
		return rawMaterials;
	}
	public void setRawMaterials(List<ManuRawMaterial> rawMaterials) {
		this.rawMaterials = rawMaterials;
	}
    
}

