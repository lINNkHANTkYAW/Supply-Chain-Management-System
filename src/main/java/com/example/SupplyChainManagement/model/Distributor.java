package com.example.SupplyChainManagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "distributors")
public class Distributor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "distributor_id")
    private Long distributorId;

    @OneToOne
    @JoinColumn(name = "user_did", referencedColumnName = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "role", "createdAt", "updatedAt"})  // Prevent circular reference
    private User user;

    @Column(name = "company_name")
    private String companyName;
    
    @Column(name = "contact_info")
	private String contactInfo;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "bio")
    private String bio;
    
    @Column(name = "rating", nullable = false)
    private Double rating = 0.0; // Average rating, default to 0.0

    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0;
    
    // Constructors, Getters, Setters...
	public Long getDistributorId() {
		return distributorId;
	}
	public void setDistributorId(Long distributorId) {
		this.distributorId = distributorId;
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
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public Integer getRatingCount() {
		return ratingCount;
	}
	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}
}

